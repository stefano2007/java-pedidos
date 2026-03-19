package com.stefano.pedidos.endpoints.estoques;

import com.stefano.pedidos.endpoints.estoques.dto.request.AtualizarEstoqueConferenciaRequest;
import com.stefano.pedidos.endpoints.estoques.dto.request.EstoqueRequest;
import com.stefano.pedidos.endpoints.estoques.dto.response.EstoqueAtualResponse;
import com.stefano.pedidos.endpoints.estoques.dto.response.EstoqueResponse;
import com.stefano.pedidos.endpoints.estoques.service.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping()
    public ResponseEntity<Page<EstoqueResponse>> obterTodosEstoque(
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<EstoqueResponse> produtosEstoqueResponse = estoqueService.obterTodosEstoque(pageable);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @PostMapping
    public ResponseEntity<List<EstoqueResponse>> criarEstoque(@Valid @RequestBody EstoqueRequest request) {
        List<EstoqueResponse> produtosEstoqueResponse = estoqueService.criarEstoque(request);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @PutMapping("/conferencia")
    public ResponseEntity<List<EstoqueResponse>> atualizarEstoque(@Valid @RequestBody AtualizarEstoqueConferenciaRequest request) {
        List<EstoqueResponse> produtosEstoqueResponse = estoqueService.atualizarEstoque(request);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @GetMapping("{produtoId}/produto")
    public ResponseEntity<List<EstoqueResponse>> obterEstoquePorProdutoId(
            @PathVariable("produtoId") Long produtoId) {
        List<EstoqueResponse> produtosEstoqueResponse = estoqueService.obterEstoquePorId(produtoId);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @GetMapping("saldo-atual")
    public ResponseEntity<List<EstoqueAtualResponse>> obterEstoqueAtualPorProdutoId() {
        List<EstoqueAtualResponse> produtosEstoqueResponse = estoqueService.obterEstoqueAtual();
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }

    @GetMapping("{produtoId}/produto-saldo-atual")
    public ResponseEntity<EstoqueAtualResponse> obterEstoqueAtualPorProdutoId(
            @PathVariable("produtoId") Long produtoId) {
        EstoqueAtualResponse produtosEstoqueResponse = estoqueService.obterEstoqueAtualPorId(produtoId);
        return ResponseEntity.ok().body(produtosEstoqueResponse);
    }
}
