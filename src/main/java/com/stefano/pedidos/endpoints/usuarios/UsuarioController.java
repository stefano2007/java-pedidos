package com.stefano.pedidos.endpoints.usuarios;

import com.stefano.pedidos.endpoints.usuarios.dto.request.UsuarioRequest;
import com.stefano.pedidos.endpoints.usuarios.dto.response.UsuarioResponse;
import com.stefano.pedidos.endpoints.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> obtertTodos(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable
    ) {
        Page<UsuarioResponse> usuariosResponse = usuarioService.obterTodos(pageable);
        return ResponseEntity.ok().body(usuariosResponse);
    }

    @GetMapping("{usuarioId}")
    public ResponseEntity<UsuarioResponse> obterPorId(
            @PathVariable("usuarioId") Long usuarioId) {
        UsuarioResponse usuarioResponse = usuarioService.obterPorId(usuarioId);
        return ResponseEntity.ok().body(usuarioResponse);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuarioResponse = usuarioService.criar(request);
        return ResponseEntity.ok().body(usuarioResponse);
    }
}
