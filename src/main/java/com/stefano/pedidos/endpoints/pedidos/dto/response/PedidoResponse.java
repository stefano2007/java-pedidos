package com.stefano.pedidos.endpoints.pedidos.dto.response;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long usuarioId,
        LocalDateTime dataCriacao,
        String statusItem,
        String motivoCancelamento,
        List<PedidoItemResponse> itens,
        BigDecimal valorTotal
) {
    public static PedidoResponse of(Pedido novoPedido) {
        List<PedidoItemResponse> itensResponse = novoPedido.getItens().stream()
                .map(PedidoItemResponse::of).toList();

        BigDecimal valorTotal = itensResponse.stream()
                .map(PedidoItemResponse::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponse(novoPedido.getId(), novoPedido.getUsuario().getId(), novoPedido.getDataCriacao(),
                novoPedido.getStatus().name(), novoPedido.getMotivoCancelamento(), itensResponse, valorTotal);
    }
}
