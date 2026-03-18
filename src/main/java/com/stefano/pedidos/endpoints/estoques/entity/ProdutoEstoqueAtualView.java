package com.stefano.pedidos.endpoints.estoques.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "VW_PRODUTO_ESTOQUE_ATUAL")
public class ProdutoEstoqueAtualView {

    @Id
    @Column(name = "produto_id")
    private Long produtoId;

    @Column(name = "nome")
    private String nomeProduto;

    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque;

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

}
