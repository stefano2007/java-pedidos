package com.stefano.pedidos.endpoints.estoques.service;

import com.stefano.pedidos.endpoints.estoques.dto.request.AtualizarEstoqueConferenciaRequest;
import com.stefano.pedidos.endpoints.estoques.dto.request.EstoqueRequest;
import com.stefano.pedidos.endpoints.estoques.dto.response.EstoqueAtualResponse;
import com.stefano.pedidos.endpoints.estoques.dto.response.EstoqueResponse;
import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoque;
import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoqueAtualView;
import com.stefano.pedidos.endpoints.estoques.entity.StatusEstoque;
import com.stefano.pedidos.endpoints.estoques.repository.ProdutoEstoqueAtualViewRepository;
import com.stefano.pedidos.endpoints.estoques.repository.ProdutoEstoqueRepository;
import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import com.stefano.pedidos.endpoints.produtos.service.ProdutoService;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.endpoints.usuarios.service.UsuarioService;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueService {

    private final ProdutoEstoqueRepository produtoEstoqueRepository;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;
    private final ProdutoEstoqueAtualViewRepository produtoEstoqueAtualViewRepository;

    public EstoqueService(ProdutoEstoqueRepository produtoEstoqueRepository, ProdutoService produtoService,
                          UsuarioService usuarioService, ProdutoEstoqueAtualViewRepository produtoEstoqueAtualViewRepository) {
        this.produtoEstoqueRepository = produtoEstoqueRepository;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
        this.produtoEstoqueAtualViewRepository = produtoEstoqueAtualViewRepository;
    }

    public List<EstoqueResponse> obterEstoquePorId(Long produtoId) {
        return produtoEstoqueRepository
                .findByProdutoId(produtoId)
                .stream()
                .map(EstoqueResponse::of)
                .toList();
    }

    public Page<EstoqueResponse> obterObterTodosEstoque(
            Pageable pageable
    ) {
        return produtoEstoqueRepository
                .findAll(pageable)
                .map(EstoqueResponse::of);
    }

    public List<EstoqueResponse> criarEstoque(EstoqueRequest request) {

        // validar usuário
        Usuario usuario = usuarioService.obterUsuarioOuExcecao(request.usuarioId());

        List<ProdutoEstoque> novosProdutosEstoque = new ArrayList<>();
        for (EstoqueRequest.ProdutoEstoqueItemRequest item : request.itens()) {
            Produto produto = produtoService.obterProdutoOuExcecao(item.produtoId());

            ProdutoEstoque novoProdutoEstoque = ProdutoEstoque.criarProdutoEstoque(produto, usuario, item.quantidade(), item.tipo());
            novosProdutosEstoque.add(novoProdutoEstoque);
        }

        produtoEstoqueRepository.saveAll(novosProdutosEstoque);
        return novosProdutosEstoque.stream().map(EstoqueResponse::of).toList();
    }


    @Transactional
    public List<EstoqueResponse> atualizarEstoque(@Valid AtualizarEstoqueConferenciaRequest request) {
        Usuario usuarioConferencia = usuarioService.obterUsuarioOuExcecao(request.usuarioId());

        List<ProdutoEstoque> produtosAtualizados = request.itens().stream()
                .map(item -> atualizarItemEstoque(item, usuarioConferencia))
                .toList();

        produtoEstoqueRepository.saveAll(produtosAtualizados);
        return produtosAtualizados.stream()
                .map(EstoqueResponse::of)
                .toList();
    }

    private ProdutoEstoque atualizarItemEstoque(
            AtualizarEstoqueConferenciaRequest.AtualizarProdutoEstoqueConferenciaItemRequest item,
            Usuario usuarioConferencia) {
        ProdutoEstoque produtoEstoque = produtoEstoqueRepository.findById(item.produtoEstoqueId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto Estoque não encontrado: %d".formatted(item.produtoEstoqueId())));

        produtoEstoque.atualizarStatus(StatusEstoque.from(item.statusEstoque()), usuarioConferencia, item.quantidadeConferida());
        return produtoEstoque;
    }

    public List<EstoqueAtualResponse> obterEstoqueAtual() {
        return produtoEstoqueAtualViewRepository.findAll().stream()
                .map(EstoqueAtualResponse::of)
                .toList();
    }

    public EstoqueAtualResponse obterEstoqueAtualPorId(Long produtoId) {
        ProdutoEstoqueAtualView produtoEstoqueAtualView = this.obterProdutoEstoqueAtualOuExcecao(produtoId);
        return EstoqueAtualResponse.of(produtoEstoqueAtualView);
    }

    public Optional<ProdutoEstoqueAtualView> obterProdutoEstoqueAtualView(Long produtoId) {
        return produtoEstoqueAtualViewRepository.findById(produtoId);
    }

    public ProdutoEstoqueAtualView obterProdutoEstoqueAtualOuExcecao(Long produtoId) {
        return this.obterProdutoEstoqueAtualView(produtoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Produto Estoque Atual não encontrado para o produtoId: %s".formatted(produtoId)));
    }

    public ProdutoEstoque obterProdutoEstoqueOuExcecao(Long produtoEstoqueId) {
        return produtoEstoqueRepository.findById(produtoEstoqueId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Produto Estoque não encontrado: %s".formatted(produtoEstoqueId)));
    }

}
