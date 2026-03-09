package com.stefano.pedidos.endpoints.pedidos.entity;

import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PEDIDO_ITENS")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 18, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "quantidade_atendida")
    private Integer quantidadeAtendida;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_item")
    private StatusPedidoItem statusItem;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    protected PedidoItem() {
    }

    public static PedidoItem criarItemPedidoCriadoOuCancelado(Pedido pedido, Produto produto, Integer quantidade, Long produtoIdSolicitado) {
        PedidoItem novoPedidoItem = new PedidoItem();
        novoPedidoItem.pedido = pedido;
        novoPedidoItem.quantidade = quantidade;

        if (produto == null){
            novoPedidoItem.precoUnitario = new BigDecimal(0);
            novoPedidoItem.cancelarItem("Produto não encontrado: %d".formatted(produtoIdSolicitado));
        } else {
            novoPedidoItem.produto = produto;
            novoPedidoItem.precoUnitario = produto.getPreco();
            novoPedidoItem.statusItem = StatusPedidoItem.CRIADO;
        }

        return novoPedidoItem;
    }

    public boolean criado() {
        return StatusPedidoItem.CRIADO.equals(this.statusItem);
    }


    public void validarPeditoItem() {
        if (this.statusItem.podeIrPara(StatusPedidoItem.VALIDADO)){
            this.alterarStatusItem(StatusPedidoItem.VALIDADO);
        }
    }

    public void reservarEstoque(Integer quantidadeAtendida) {
        if (quantidadeAtendida > quantidade) {
            throw new IllegalStateException(
                    "Pedido Item %s, Quantidade conferida (%s) não pode ser maior que a quantidade do item (%s)"
                            .formatted(this.id, quantidadeAtendida, quantidade)
            );
        }
        alterarStatusItem(StatusPedidoItem.RESERVADO_ESTOQUE);
        this.quantidadeAtendida = quantidadeAtendida;
    }

    public void semEstoque(String motivoCancelamento) {
        alterarStatusItem(StatusPedidoItem.SEM_ESTOQUE);
        this.quantidadeAtendida = 0;
        this.motivoCancelamento = motivoCancelamento;
    }

    public void cancelarItem(String motivoCancelamento) {
        alterarStatusItem(StatusPedidoItem.CANCELADO);
        this.quantidadeAtendida = 0;
        this.motivoCancelamento = motivoCancelamento;
    }

    public void alterarStatusItem(StatusPedidoItem novoStatus) {

        if (this.statusItem != null && !this.statusItem.podeIrPara(novoStatus)) {
            throw new IllegalStateException(
                    "Pedido Item %s, Transição inválida de %s para %s"
                            .formatted(this.id, this.statusItem, novoStatus)
            );
        }

        this.statusItem = novoStatus;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public Integer getQuantidadeAtendida() {
        return quantidadeAtendida;
    }

    public StatusPedidoItem getStatusItem() {
        return statusItem;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }


}
