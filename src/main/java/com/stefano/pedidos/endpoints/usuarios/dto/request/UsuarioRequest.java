package com.stefano.pedidos.endpoints.usuarios.dto.request;

import com.stefano.pedidos.endpoints.usuarios.validation.annotation.SenhaValida;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        String email,

        @SenhaValida
        String senha,

        String confirmacaoSenha
) {
}
