package com.stefano.pedidos.endpoints.pedidos.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ReservarEstoquePedidoRequest(
        @NotNull(message = "Usuário é obrigatório")
        @Positive(message = "Usuário inválido")
        Long usuarioId,

        @NotNull(message = "Pedido é obrigatório")
        @Positive(message = "Pedido inválido")
        Long pedidoId,

        @NotEmpty(message = "O pedido deve possuir ao menos um item")
        @Valid
        List<ReservarEstoquePedidoItemRequest> itens
) {
    public record ReservarEstoquePedidoItemRequest(
            @NotNull(message = "Pedido Item é obrigatório")
            Long pedidoItemId,

            @NotNull(message = "Quantidade Conferida é obrigatório")
            @Min(value = 1, message = "Quantidade Conferida deve ser maior que 0")
            Integer quantidadeConferida
    ) {
    }
}
