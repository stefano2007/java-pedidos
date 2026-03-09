package com.stefano.pedidos.endpoints.pedidos.service;

import com.stefano.pedidos.endpoints.pedidos.entity.*;
import com.stefano.pedidos.endpoints.pedidos.dto.request.ValidarPedidoRequest;
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
        return pedidoRepository.findById(pedidoId)
                .map(PedidoResponse::of)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Pedido não encontrado"));
    }

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest request) {

        // validar usuário
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

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
    public PedidoResponse validarPedido(ValidarPedidoRequest request) {

        //validar pedido
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Pedido não encontrado"));

        pedido.alterarStatusValidado();

        this.pedidoRepository.save(pedido);
        this.pedidoProducer.publicar(pedido);

        return PedidoResponse.of(pedido);
    }


    @Transactional
    public synchronized Pedido reservarEstoquePedido(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: %s".formatted(pedidoId)));

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
}
