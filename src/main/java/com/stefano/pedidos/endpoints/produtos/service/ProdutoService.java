package com.stefano.pedidos.endpoints.produtos.service;

import com.stefano.pedidos.endpoints.produtos.dto.request.ProdutoRequest;
import com.stefano.pedidos.endpoints.produtos.dto.response.ProdutoResponse;
import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoRepository;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Page<ProdutoResponse> obterTodos(
            Pageable pageable
    ) {
        return produtoRepository
                .findAll(pageable)
                .map(ProdutoResponse::of);
    }

    public ProdutoResponse obterPorId(Long produtoId) {
        return ProdutoResponse.of(obterProdutoOuExcecao(produtoId));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Produto novoProduto = Produto.criarProduto(request.nome(), request.descricao(), request.preco());
        produtoRepository.save(novoProduto);

        return ProdutoResponse.of(novoProduto);
    }

    public void inativarProduto(Long produtoId) {
        Produto produto = obterProdutoOuExcecao(produtoId);
        produto.inativar();
        produtoRepository.save(produto);
    }

    public Optional<Produto> obterProduto(Long produtoId) {
        return produtoRepository.findById(produtoId);
    }

    public Produto obterProdutoOuExcecao(Long produtoId) {
        return this.obterProduto(produtoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Produto não encontrado: %s".formatted(produtoId)));
    }
}
