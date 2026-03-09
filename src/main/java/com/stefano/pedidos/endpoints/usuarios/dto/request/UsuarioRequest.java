package com.stefano.pedidos.endpoints.usuarios.dto.request;

import com.stefano.pedidos.endpoints.usuarios.validation.annotation.SenhaValida;
import jakarta.validation.constraints.*;

public record UsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        @SenhaValida
        String senha,

        String confirmacaoSenha
) {}
