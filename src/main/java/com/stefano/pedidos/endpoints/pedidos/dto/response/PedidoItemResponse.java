package com.stefano.pedidos.endpoints.pedidos.dto.response;

import com.stefano.pedidos.endpoints.pedidos.entity.PedidoItem;
import com.stefano.pedidos.endpoints.pedidos.entity.StatusPedidoItem;
import com.stefano.pedidos.endpoints.produtos.dto.response.ProdutoResponse;

import java.math.BigDecimal;
import java.util.Optional;

public record PedidoItemResponse(
        Long id,
        ProdutoResponse produto,
        Integer quantidade,
        BigDecimal precoUnitario,
        Integer quantidadeAtendida,
        StatusPedidoItem statusItem,
        String motivoCancelamento,
        BigDecimal subTotal
) {
    public static PedidoItemResponse of(PedidoItem item) {
        ProdutoResponse produtoResponse =
                Optional.ofNullable(item.getProduto())
                        .map(ProdutoResponse::of)
                        .orElse(null);

        BigDecimal subTotal = item.getPrecoUnitario() != null && item.getQuantidadeAtendida() != null
                ? item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidadeAtendida()))
                : BigDecimal.ZERO;

        return new PedidoItemResponse(
                item.getId(),
                produtoResponse,
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getQuantidadeAtendida(),
                item.getStatusItem(),
                item.getMotivoCancelamento(),
                subTotal
        );
    }
}
