package com.stefano.pedidos.endpoints.produtos;

import com.stefano.pedidos.endpoints.produtos.dto.request.ProdutoRequest;
import com.stefano.pedidos.endpoints.produtos.dto.response.ProdutoResponse;
import com.stefano.pedidos.endpoints.produtos.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @PatchMapping("{produtoId}/inativar")
    public ResponseEntity<Void> inativarProduto(@PathVariable("produtoId") Long produtoId) {

        produtoService.inativarProduto(produtoId);

        return ResponseEntity.noContent().build();
    }
}
