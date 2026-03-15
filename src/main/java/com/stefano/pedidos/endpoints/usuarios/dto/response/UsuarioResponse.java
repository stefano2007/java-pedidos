package com.stefano.pedidos.endpoints.usuarios.dto.response;

import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(String nome, String email, LocalDateTime dataCriacao, Boolean ativo) {
    public static UsuarioResponse of(Usuario usuario) {
        return new UsuarioResponse(usuario.getNome(), usuario.getEmail(), usuario.getDataCriacao(), usuario.getAtivo());
    }
}
