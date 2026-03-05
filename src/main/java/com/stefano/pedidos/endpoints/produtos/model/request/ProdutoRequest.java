package com.stefano.pedidos.endpoints.produtos.model.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record ProdutoRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 5, max = 255, message = "Descrição deve ter entre 5 e 255 caracteres")
        String descricao,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        @Digits(integer = 18, fraction = 2, message = "Preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal preco
) {
}
