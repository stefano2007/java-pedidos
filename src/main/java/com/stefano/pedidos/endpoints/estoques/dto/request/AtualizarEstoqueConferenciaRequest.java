package com.stefano.pedidos.endpoints.estoques.dto.request;

import com.stefano.pedidos.endpoints.estoques.validation.annotation.StatusConferenciaValido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record AtualizarEstoqueConferenciaRequest(
        @NotNull(message = "Usuário é obrigatório")
        @Positive(message = "Usuário inválido")
        Long usuarioId,

        @NotEmpty(message = "O produto estoque deve possuir ao menos um item")
        @Valid
        List<AtualizarProdutoEstoqueConferenciaItemRequest> itens
) {
    public record AtualizarProdutoEstoqueConferenciaItemRequest(
            @NotNull(message = "ProdutoEstoqueId é obrigatório")
            Long produtoEstoqueId,

            @NotNull(message = "Quantidade conferida é obrigatória")
            @PositiveOrZero(message = "Quantidade conferida não pode ser negativa")
            Integer quantidadeConferida,

            @NotNull(message = "Status estoque é obrigatório")
            @StatusConferenciaValido
            String statusEstoque
    ){}
}
