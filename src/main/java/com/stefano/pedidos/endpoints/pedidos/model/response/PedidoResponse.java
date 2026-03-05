package com.stefano.pedidos.endpoints.pedidos.model.response;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.entity.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long usuarioId,
        LocalDateTime dataCriacao,
        StatusPedido statusItem,
        String motivoCancelamento,
        List<PedidoItemResponse> itens
) {
    public static PedidoResponse of(Pedido novoPedido) {
        List<PedidoItemResponse> itensResponse = novoPedido.getItens().stream()
                .map(PedidoItemResponse::of).toList();

        return new PedidoResponse(novoPedido.getId(), novoPedido.getUsuario().getId(), novoPedido.getDataCriacao(),
                novoPedido.getStatus(), novoPedido.getMotivoCancelamento(), itensResponse);
    }
}
