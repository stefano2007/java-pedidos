package com.stefano.pedidos.endpoints.produtos.service;

import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import com.stefano.pedidos.endpoints.produtos.entity.ProdutoEstoque;
import com.stefano.pedidos.endpoints.produtos.entity.StatusEstoque;
import com.stefano.pedidos.endpoints.produtos.dto.request.AtualizarProdutoEstoqueConferenciaRequest;
import com.stefano.pedidos.endpoints.produtos.dto.request.ProdutoEstoqueRequest;
import com.stefano.pedidos.endpoints.produtos.dto.response.ProdutoEstoqueResponse;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoEstoqueRepository;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.endpoints.usuarios.repository.UsuarioRepository;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import com.stefano.pedidos.endpoints.produtos.dto.request.ProdutoRequest;
import com.stefano.pedidos.endpoints.produtos.dto.response.ProdutoResponse;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoEstoqueRepository produtoEstoqueRepository;
    private final UsuarioRepository usuarioRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoEstoqueRepository produtoEstoqueRepository,
                          UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoEstoqueRepository = produtoEstoqueRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<ProdutoResponse> obterTodos(
            Pageable pageable
    ) {
        return produtoRepository
                .findAll(pageable)
                .map(ProdutoResponse::of);
    }

    public ProdutoResponse obterPorId(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .map(ProdutoResponse::of)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Produto não encontrado"));
    }

    public ProdutoResponse criar(ProdutoRequest request) {
        Produto novoProduto = Produto.criarProduto(request.nome(), request.descricao(), request.preco());
        produtoRepository.save(novoProduto);

        return ProdutoResponse.of(novoProduto);
    }

    public List<ProdutoEstoqueResponse> obterEstoquePorId(Long produtoId) {
        return produtoEstoqueRepository
                .findByProdutoId(produtoId)
                .stream()
                .map(ProdutoEstoqueResponse::of)
                .toList();
    }

    public Page<ProdutoEstoqueResponse> obterObterTodosEstoque(
            Pageable pageable
    ) {
        return produtoEstoqueRepository
                .findAll(pageable)
                .map(ProdutoEstoqueResponse::of);
    }

    public List<ProdutoEstoqueResponse> criarEstoque(ProdutoEstoqueRequest request) {

        // validar usuário
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        List<ProdutoEstoque> novosProdutosEstoque = new ArrayList<>();
        for (ProdutoEstoqueRequest.ProdutoEstoqueItemRequest item : request.itens()) {
            Produto produto = produtoRepository.findById(item.produtoId())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException("Produto não encontrado: %d".formatted(item.produtoId())));

            ProdutoEstoque novoProdutoEstoque = ProdutoEstoque.criarProdutoEstoque(produto, usuario, item.quantidade(), item.tipo());
            novosProdutosEstoque.add(novoProdutoEstoque);
        }

        produtoEstoqueRepository.saveAll(novosProdutosEstoque);
        return novosProdutosEstoque.stream().map(ProdutoEstoqueResponse::of).toList();
    }


    @Transactional
    public List<ProdutoEstoqueResponse> atualizarEstoque(@Valid AtualizarProdutoEstoqueConferenciaRequest request) {
        Usuario usuarioConferencia = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        List<ProdutoEstoque> produtosAtualizados = request.itens().stream()
                .map(item -> atualizarItemEstoque(item, usuarioConferencia))
                .toList();

        produtoEstoqueRepository.saveAll(produtosAtualizados);
        return produtosAtualizados.stream()
                .map(ProdutoEstoqueResponse::of)
                .toList();
    }

    private ProdutoEstoque atualizarItemEstoque(
            AtualizarProdutoEstoqueConferenciaRequest.AtualizarProdutoEstoqueConferenciaItemRequest item,
            Usuario usuarioConferencia) {
        ProdutoEstoque produtoEstoque = produtoEstoqueRepository.findById(item.produtoEstoqueId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto Estoque não encontrado: %d".formatted(item.produtoEstoqueId())));

        produtoEstoque.atualizarStatus(StatusEstoque.from(item.statusEstoque()), usuarioConferencia, item.quantidadeConferida());
        return produtoEstoque;
    }

}
