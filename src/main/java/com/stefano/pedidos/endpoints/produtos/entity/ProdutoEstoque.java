package com.stefano.pedidos.endpoints.produtos.entity;

import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "produtos_estoque")
public class ProdutoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque = 0;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_estoque")
    private StatusEstoque statusEstoque = StatusEstoque.CRIADO;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_conferencia_id", nullable = false)
    private Usuario usuarioConferencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 60, nullable = false)
    private TipoMovimentacaoEstoque tipo;

    protected ProdutoEstoque() {
    }

    public static ProdutoEstoque criarProdutoEstoque(Produto produto, Usuario usuario, Integer quantidade, TipoMovimentacaoEstoque tipo) {
        ProdutoEstoque novoProdutoEstoque = new ProdutoEstoque();

        novoProdutoEstoque.produto = produto;
        novoProdutoEstoque.usuario = usuario;
        novoProdutoEstoque.quantidade = quantidade;
        novoProdutoEstoque.quantidadeEstoque = 0;
        novoProdutoEstoque.statusEstoque = StatusEstoque.CRIADO;
        novoProdutoEstoque.dataCriacao = LocalDateTime.now();
        novoProdutoEstoque.tipo = tipo;

        return novoProdutoEstoque;
    }

    public void conferirEstoque(Usuario usuarioConferencia, Integer novaQuantidade) {
        if (novaQuantidade > quantidade) {
            throw new IllegalStateException(
                    "Produto Estoque %s, Quantidade conferida (%s) não pode ser maior que a quantidade do item (%s)"
                            .formatted(this.id, novaQuantidade, quantidade)
            );
        }
        this.alterarStatus(StatusEstoque.CONFERIDO);
        this.quantidadeEstoque = novaQuantidade;
        this.usuarioConferencia = usuarioConferencia;
    }

    public void rejeitaEstoque(Usuario usuarioConferencia) {
        this.alterarStatus(StatusEstoque.REJEITADO);
        this.quantidadeEstoque = 0;
        this.usuarioConferencia = usuarioConferencia;
    }

    public void alterarStatus(StatusEstoque novoStatus) {

        if (!this.statusEstoque.podeIrPara(novoStatus)) {
            throw new IllegalStateException(
                    "Produto Estoque %s, Transição inválida de %s para %s"
                            .formatted(this.id, this.statusEstoque, novoStatus)
            );
        }

        this.statusEstoque = novoStatus;
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public StatusEstoque getStatusEstoque() {
        return statusEstoque;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Usuario getUsuarioConferencia() {
        return usuarioConferencia;
    }

    public TipoMovimentacaoEstoque getTipo() {
        return tipo;
    }
}