package com.stefano.pedidos.endpoints.produtos.entity;

import com.stefano.pedidos.endpoints.pedidos.entity.PedidoItem;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "PRODUTOS")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @OneToMany(mappedBy = "produto")
    private List<PedidoItem> itens;

    protected Produto() {}

    public static Produto criarProduto(String nome, String descricao, BigDecimal preco){
        Produto novoProduto = new Produto();
        novoProduto.nome = nome;
        novoProduto.descricao = descricao;
        novoProduto.preco = preco;

        //todo: validar regras para produto
        return  novoProduto;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }
}
