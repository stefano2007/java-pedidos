package com.stefano.pedidos.endpoints.estoques.dto.response;

import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoqueAtualView;

public record EstoqueAtualResponse (
        Long produtoId,
        String nomeProduto,
        Integer quantidadeDisponivel
){
    public static EstoqueAtualResponse of(ProdutoEstoqueAtualView produtoEstoqueAtualView) {
        return new EstoqueAtualResponse(
                produtoEstoqueAtualView.getProdutoId(),
                produtoEstoqueAtualView.getNomeProduto(),
                produtoEstoqueAtualView.getQuantidadeEstoque()
        );
    }
}
