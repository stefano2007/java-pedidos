package com.stefano.pedidos.endpoints.pedidos.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedido_status")
public class PedidoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    protected PedidoStatus() {
    }

    public static PedidoStatus criarPedidoStatus(Pedido pedido){
        PedidoStatus novoPedidoStatus = new PedidoStatus();
        novoPedidoStatus.pedido = pedido;
        novoPedidoStatus.status = pedido.getStatus();
        novoPedidoStatus.dataCriacao = LocalDateTime.now();

        return novoPedidoStatus;
    }

    public Long getId() {
        return id;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Pedido getPedido() {
        return pedido;
    }
}