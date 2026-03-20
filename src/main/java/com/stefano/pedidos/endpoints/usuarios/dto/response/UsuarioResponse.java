package com.stefano.pedidos.endpoints.usuarios.dto.response;

import com.stefano.pedidos.endpoints.usuarios.entity.Role;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponse(Long id, String nome, String email, LocalDateTime dataCriacao, Boolean ativo,
                              List<String> permissoes) {
    public static UsuarioResponse of(Usuario usuario) {
        List<String> permissoes = usuario.getRoles()
                .stream()
                .map(Role::getNome)
                .toList();
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getDataCriacao(), usuario.getAtivo(), permissoes);
    }
}
