package com.stefano.pedidos.endpoints.pedidos.service;

import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoqueAtualView;
import com.stefano.pedidos.endpoints.estoques.service.EstoqueService;
import com.stefano.pedidos.endpoints.pedidos.dto.request.CancelarPedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.dto.request.PedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.dto.response.PedidoResponse;
import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.entity.PedidoItem;
import com.stefano.pedidos.endpoints.pedidos.entity.StatusPedidoItem;
import com.stefano.pedidos.endpoints.pedidos.repository.PedidoRepository;
import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import com.stefano.pedidos.endpoints.produtos.service.ProdutoService;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.endpoints.usuarios.service.UsuarioService;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import com.stefano.pedidos.kafka.producer.PedidoProducer;
import com.stefano.pedidos.util.CustomerInfo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;
    private final PedidoProducer pedidoProducer;
    private final CustomerInfo customerInfo;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioService usuarioService, ProdutoService produtoService,
                         EstoqueService estoqueService, PedidoProducer pedidoProducer, CustomerInfo customerInfo) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioService = usuarioService;
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
        this.pedidoProducer = pedidoProducer;
        this.customerInfo = customerInfo;
    }

    public Page<PedidoResponse> obterTodos(Pageable pageable) {
        return pedidoRepository.findAll(pageable)
                .map(PedidoResponse::of);
    }

    public PedidoResponse obterPorId(Long pedidoId) {
        Pedido pedido = obterPedidoOuExcecao(pedidoId);

        logger.info("Pedido retornado com sucesso: {}", pedidoId);
        return PedidoResponse.of(pedido);
    }

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest request) {

        // validar usuário
        Long usuarioId = customerInfo.obterUsuariIdOuNulo();
        if (usuarioId == null) {
            throw new RecursoNaoEncontradoException("Usuário autenticado não encontrado para criar o pedido.");
        }
        Usuario usuario = usuarioService.obterUsuarioOuExcecao(usuarioId);

        Pedido novoPedido = Pedido.criarPedido(usuario);

        List<PedidoItem> pedidoItens = request.itens().stream()
                .map(itemRequest -> {
                    Produto produto = produtoService.obterProduto(itemRequest.produtoId())
                            .orElse(null);

                    return PedidoItem.criarItemPedidoCriadoOuCancelado(
                            novoPedido,
                            produto,
                            itemRequest.quantidade(),
                            itemRequest.produtoId()
                    );
                })
                .toList();

        novoPedido.adicionarPedidoItens(pedidoItens);
        pedidoRepository.save(novoPedido);

        logger.info("Pedido criado: {}, Usuario: {}", novoPedido.getId(), usuario.getId());
        return PedidoResponse.of(novoPedido);
    }

    @Transactional
    public PedidoResponse validarPedido(Long pedidoId) {

        //validar pedido
        Pedido pedido = obterPedidoOuExcecao(pedidoId);

        pedido.alterarStatusValidado();

        this.pedidoRepository.save(pedido);
        this.pedidoProducer.publicar(pedido);

        logger.info("Pedido validado: {}", pedido.getId());
        return PedidoResponse.of(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedido(Long pedidoId, CancelarPedidoRequest request) {

        //validar pedido
        Pedido pedido = obterPedidoOuExcecao(pedidoId);

        Long usuarioId = customerInfo.obterUsuariIdOuNulo();
        if (usuarioId == null) {
            throw new RecursoNaoEncontradoException("Usuário autenticado não encontrado para cancelar o pedido.");
        }

        final String mensagemCompleto = "Id Usuario Cancelamento: %s, Motivo do cancelamento: %s"
                .formatted(usuarioId, request.motivoCancelamento());

        pedido.cancelar(mensagemCompleto);

        this.pedidoRepository.save(pedido);
        this.pedidoProducer.publicar(pedido);

        logger.info("Pedido cancelado: {}, {}", pedido.getId(), mensagemCompleto);
        return PedidoResponse.of(pedido);
    }


    @Transactional
    public synchronized Pedido reservarEstoquePedido(Long pedidoId) {

        Pedido pedido = obterPedidoOuExcecao(pedidoId);

        for (PedidoItem item : pedido.getItens().stream().filter(i -> i.getStatusItem() == StatusPedidoItem.VALIDADO).toList()) {

            final Integer estoqueAtual = estoqueService.obterProdutoEstoqueAtualView(item.getProduto().getId())
                    .map(ProdutoEstoqueAtualView::getQuantidadeEstoque)
                    .orElse(0);

            final int quantidadeAtendida = estoqueAtual >= item.getQuantidade()
                    ? item.getQuantidade()
                    : Math.max(estoqueAtual, 0);

            if (quantidadeAtendida > 0) {
                item.reservarEstoque(quantidadeAtendida);
            } else {
                item.semEstoque("Estoque insuficiente");
            }
        }

        pedido.alterarStatusReservadoEstoqueOuCancelar();
        pedidoRepository.save(pedido);

        logger.info("Reserva de estoque processada para Pedido: {}, Status: {}", pedido.getId(), pedido.getStatus());
        return pedido;
    }

    public Optional<Pedido> obterPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId);
    }

    public Pedido obterPedidoOuExcecao(Long pedidoId) {
        return this.obterPedido(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: %s".formatted(pedidoId)));
    }
}
