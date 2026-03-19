package com.stefano.pedidos.endpoints.pedidos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PedidoRequest(
        @NotEmpty(message = "O pedido deve possuir ao menos um item")
        @Valid
        List<PedidoItemRequest> itens
) {
    public record PedidoItemRequest(
            @NotNull(message = "ProdutoId é obrigatório")
            Long produtoId,

            @NotNull(message = "Quantidade é obrigatório")
            @Min(value = 1, message = "Quantidade deve ser maior que 0")
            Integer quantidade
    ) {
    }
}
