package com.stefano.pedidos.endpoints.produtos.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "VW_PRODUTO_ESTOQUE_ATUAL")
public class ProdutoEstoqueAtualView {

    @Id
    @Column(name = "produto_id")
    private Long produtoId;


    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque;

    public Long getProdutoId() {
        return produtoId;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
}
