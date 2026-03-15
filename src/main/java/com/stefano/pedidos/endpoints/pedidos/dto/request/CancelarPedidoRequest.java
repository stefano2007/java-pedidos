package com.stefano.pedidos.endpoints.pedidos.dto.request;

import jakarta.validation.constraints.NotNull;

public record CancelarPedidoRequest (
        @NotNull(message = "motivo cancelamento é obrigatório")
        String motivoCancelamento
){
}
