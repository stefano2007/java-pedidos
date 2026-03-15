package com.stefano.pedidos.endpoints.pedidos.service;

import com.stefano.pedidos.config.model.UserPrincipal;
import com.stefano.pedidos.endpoints.pedidos.dto.request.CancelarPedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.entity.*;
import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import com.stefano.pedidos.endpoints.produtos.entity.ProdutoEstoqueAtualView;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoEstoqueAtualViewRepository;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import com.stefano.pedidos.endpoints.pedidos.dto.request.PedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.dto.response.PedidoResponse;
import com.stefano.pedidos.endpoints.pedidos.repository.PedidoRepository;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoRepository;
import com.stefano.pedidos.endpoints.usuarios.repository.UsuarioRepository;
import com.stefano.pedidos.kafka.producer.PedidoProducer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoEstoqueAtualViewRepository produtoEstoqueAtualViewRepository;
    private final PedidoProducer pedidoProducer;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository,
                         ProdutoEstoqueAtualViewRepository produtoEstoqueAtualViewRepository, PedidoProducer pedidoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.produtoEstoqueAtualViewRepository = produtoEstoqueAtualViewRepository;
        this.pedidoProducer = pedidoProducer;
    }

    public Page<PedidoResponse> obterTodos(Pageable pageable) {
        return pedidoRepository.findAll(pageable)
                .map(PedidoResponse::of);
    }

    public PedidoResponse obterPorId(Long pedidoId) {
        return PedidoResponse.of(tentaObterPedido(pedidoId));
    }

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest request) {

        // validar usuário
        Usuario usuario = tentaObterUsuario(request.usuarioId());

        Pedido novoPedido = Pedido.criarPedido(usuario);

        List<PedidoItem> pedidoItens = request.itens().stream()
                .map(itemRequest -> {
                    Produto produto = produtoRepository
                            .findById(itemRequest.produtoId())
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

        return PedidoResponse.of(novoPedido);
    }

    @Transactional
    public PedidoResponse validarPedido(Long pedidoId) {

        //validar pedido
        Pedido pedido = tentaObterPedido(pedidoId);

        pedido.alterarStatusValidado();

        this.pedidoRepository.save(pedido);
        this.pedidoProducer.publicar(pedido);

        return PedidoResponse.of(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedido(Long pedidoId, CancelarPedidoRequest request) {

        //validar pedido
        Pedido pedido = tentaObterPedido(pedidoId);

        UserPrincipal usuarioLogado = obterUsuarioLogado();
        final String mensagemCompleto = "Id Usuario Cancelamento: %s, Motivo do cancelamento: %s"
                .formatted(usuarioLogado.getUsuarioId(), request.motivoCancelamento());

        pedido.cancelar(mensagemCompleto);

        this.pedidoRepository.save(pedido);
        this.pedidoProducer.publicar(pedido);

        return PedidoResponse.of(pedido);
    }


    @Transactional
    public synchronized Pedido reservarEstoquePedido(Long pedidoId) {

        Pedido pedido = tentaObterPedido(pedidoId);

        for (PedidoItem item : pedido.getItens().stream().filter(i -> i.getStatusItem() == StatusPedidoItem.VALIDADO).toList()) {

            final Integer estoqueAtual = produtoEstoqueAtualViewRepository
                    .findById(item.getProduto().getId())
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

        return pedido;
    }

    private Pedido tentaObterPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: %s".formatted(pedidoId)));
    }

    private Usuario tentaObterUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: %s".formatted(usuarioId)));
    }

    private UserPrincipal obterUsuarioLogado() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
