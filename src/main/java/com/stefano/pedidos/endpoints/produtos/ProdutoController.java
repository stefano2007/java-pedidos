package com.stefano.pedidos.endpoints.produtos;

import com.stefano.pedidos.endpoints.produtos.model.request.AtualizarProdutoEstoqueConferenciaRequest;
import com.stefano.pedidos.endpoints.produtos.model.request.ProdutoEstoqueRequest;
import com.stefano.pedidos.endpoints.produtos.model.request.ProdutoRequest;
import com.stefano.pedidos.endpoints.produtos.model.response.ProdutoEstoqueResponse;
import com.stefano.pedidos.endpoints.produtos.model.response.ProdutoResponse;
import com.stefano.pedidos.endpoints.produtos.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> obtertTodos(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        Page<ProdutoResponse> produtosReponse = produtoService.obterTodos(pageable);
        return ResponseEntity.ok().body(produtosReponse);
    }

    @GetMapping("{produtoId}")
    public ResponseEntity<ProdutoResponse> obterPorId(
            @PathVariable("produtoId") Long produtoId) {
        ProdutoResponse produtoResponse = produtoService.obterPorId(produtoId);
        return ResponseEntity.ok().body(produtoResponse);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse produtoResponse = produtoService.criar(request);
        return ResponseEntity.ok().body(produtoResponse);
    }

    @GetMapping("{produtoId}/estoque")
    public ResponseEntity<List<ProdutoEstoqueResponse>> obterEstoquePorId(
            @PathVariable("produtoId") Long produtoId) {
        List<ProdutoEstoqueResponse> produtosEstoqueResponse = produtoService.obterEstoquePorId(produtoId);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @GetMapping("estoque")
    public ResponseEntity<Page<ProdutoEstoqueResponse>> obterObterTodosEstoque(
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ProdutoEstoqueResponse> produtosEstoqueResponse = produtoService.obterObterTodosEstoque(pageable);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @PostMapping("/estoque")
    public ResponseEntity<List<ProdutoEstoqueResponse>> criarEstoque(@Valid @RequestBody ProdutoEstoqueRequest request) {
        List<ProdutoEstoqueResponse> produtosEstoqueResponse = produtoService.criarEstoque(request);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @PutMapping("/estoque")
    public ResponseEntity<List<ProdutoEstoqueResponse>> atualizarEstoque(@Valid @RequestBody AtualizarProdutoEstoqueConferenciaRequest request) {
        List<ProdutoEstoqueResponse> produtosEstoqueResponse = produtoService.atualizarEstoque(request);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }
}
