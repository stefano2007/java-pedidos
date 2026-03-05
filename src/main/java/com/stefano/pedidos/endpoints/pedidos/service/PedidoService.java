package com.stefano.pedidos.endpoints.pedidos.service;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.entity.PedidoItem;
import com.stefano.pedidos.endpoints.pedidos.entity.StatusPedidoItem;
import com.stefano.pedidos.endpoints.pedidos.model.request.ReservarEstoquePedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.model.request.ValidarPedidoRequest;
import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import com.stefano.pedidos.endpoints.produtos.entity.ProdutoEstoqueAtualView;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoEstoqueAtualViewRepository;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import com.stefano.pedidos.endpoints.pedidos.model.request.PedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.model.response.PedidoResponse;
import com.stefano.pedidos.endpoints.pedidos.repository.PedidoRepository;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoRepository;
import com.stefano.pedidos.endpoints.usuarios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoEstoqueAtualViewRepository produtoEstoqueAtualViewRepository;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository, ProdutoEstoqueAtualViewRepository produtoEstoqueAtualViewRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.produtoEstoqueAtualViewRepository = produtoEstoqueAtualViewRepository;
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

        // montar itens do pedido
        for (PedidoRequest.PedidoItemRequest itemRequest : request.itens()) {
            Long produtoId = itemRequest.produtoId();
            Produto produto = produtoRepository.findById(produtoId).orElse(null);

            PedidoItem pedidoItem;

            if (produto != null) {
                pedidoItem = PedidoItem.criarItemPedido(
                        novoPedido,
                        produto,
                        itemRequest.quantidade()
                );
            } else {
                pedidoItem = PedidoItem.criarItemPedidoCancelado(
                        novoPedido,
                        itemRequest.quantidade(),
                        "Produto não encontrado: %d".formatted(produtoId)
                );
            }

            novoPedido.adicionarItem(pedidoItem);
        }

        novoPedido.verificarCancelamentoAutomatico();
        pedidoRepository.save(novoPedido);

        return PedidoResponse.of(novoPedido);
    }

    @Transactional
    public PedidoResponse validarPedido(ValidarPedidoRequest request) {

        //validar pedido
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Pedido não encontrado"));

        pedido.validarPedido();
        this.pedidoRepository.save(pedido);

        return PedidoResponse.of(pedido);
    }

    @Transactional
    public PedidoResponse reservarEstoquePedido(@Valid ReservarEstoquePedidoRequest request) {

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        for (ReservarEstoquePedidoRequest.ReservarEstoquePedidoItemRequest itemRequest : request.itens()) {

            PedidoItem item = pedido.getItens()
                    .stream()
                    .filter(i -> i.getId().equals(itemRequest.pedidoItemId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Item do pedido não encontrado: %d".formatted(itemRequest.pedidoItemId())
                            ));

            if (item.getStatusItem() != StatusPedidoItem.VALIDADO) {
                continue;
            }

            Long produtoId = item.getProduto().getId();

            Integer estoqueAtual = produtoEstoqueAtualViewRepository
                    .findById(produtoId)
                    .map(ProdutoEstoqueAtualView::getQuantidadeEstoque)
                    .orElse(0);

            if (estoqueAtual >= itemRequest.quantidadeConferida()) {
                item.reservarEstoque(itemRequest.quantidadeConferida());
            } else {
                item.semEstoque("Estoque insuficiente");
            }
        }

        //todo: revisa melhorar essa logica joga para classe pedido toda logica de conferiar produtos
        pedido.pedidoReservado();

        //todo: verificar se pode adicionar a verificação dentro da classe pedidos
        pedido.verificarCancelamentoAutomatico();

        pedidoRepository.save(pedido);

        return PedidoResponse.of(pedido);
    }
}
