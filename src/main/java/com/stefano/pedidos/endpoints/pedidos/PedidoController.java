package com.stefano.pedidos.endpoints.pedidos;

import com.stefano.pedidos.endpoints.pedidos.model.request.PedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.model.request.ReservarEstoquePedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.model.request.ValidarPedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.model.response.PedidoResponse;
import com.stefano.pedidos.endpoints.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> obtertTodos(
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PedidoResponse> pedidosReponse = pedidoService.obterTodos(pageable);
        return ResponseEntity.ok().body(pedidosReponse);
    }

    @GetMapping("{pedidoId}")
    public ResponseEntity<PedidoResponse> obterPorId(@PathVariable("pedidoId") Long pedidoId) {
        PedidoResponse pedidoReponse = pedidoService.obterPorId(pedidoId);
        return ResponseEntity.ok().body(pedidoReponse);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse pedidoResponse = pedidoService.criarPedido(request);
        return ResponseEntity.ok().body(pedidoResponse);
    }

    @PutMapping("validar")
    public ResponseEntity<PedidoResponse> validarPedido(@Valid @RequestBody ValidarPedidoRequest request) {
        PedidoResponse pedidoResponse = pedidoService.validarPedido(request);
        return ResponseEntity.ok().body(pedidoResponse);
    }

    @PutMapping("reservar-estoque")
    public ResponseEntity<PedidoResponse> reservarEstoquePedido(@Valid @RequestBody ReservarEstoquePedidoRequest request) {
        PedidoResponse pedidoResponse = pedidoService.reservarEstoquePedido(request);
        return ResponseEntity.ok().body(pedidoResponse);
    }
}
