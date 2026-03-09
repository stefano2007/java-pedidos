package com.stefano.pedidos.endpoints.produtos.dto.response;

import com.stefano.pedidos.endpoints.produtos.entity.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse(
        Long id, String nome, String descricao, BigDecimal preco, LocalDateTime dataCriacao,
        Boolean ehAtivo
) {
    public static ProdutoResponse of(Produto produto) {
        return new ProdutoResponse(produto.getId(), produto.getNome(), produto.getDescricao(), produto.getPreco(),
                produto.getDataCriacao(), produto.getAtivo());
    }
}
