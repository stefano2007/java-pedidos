package com.stefano.pedidos.endpoints.usuarios.model.request;

import jakarta.validation.constraints.*;

public record UsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        String confirmacaoSenha
) {}
