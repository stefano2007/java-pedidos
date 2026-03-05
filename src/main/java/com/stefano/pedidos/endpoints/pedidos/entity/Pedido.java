package com.stefano.pedidos.endpoints.pedidos.entity;

import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    protected Pedido() {
    }

    public static Pedido criarPedido(Usuario usuario) {
        Pedido novoPedido = new Pedido();
        novoPedido.usuario = usuario;
        novoPedido.status = StatusPedido.CRIADO;
        return novoPedido;
    }

    public void adicionarItem(PedidoItem item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        this.itens.add(item);
        item.setPedido(this);
    }

    public void validarPedido() {
        this.alterarStatus(StatusPedido.VALIDADO);
        this.itens.forEach(PedidoItem::validarPeditoItem);
    }

    public void verificarCancelamentoAutomatico() {

        if (this.status == StatusPedido.ENTREGUE) {
            return;
        }

        if (this.itens == null || this.itens.isEmpty()) {
            this.cancelar("Não existe itens para o pedido");
        }

        boolean todosCancelados = this.itens.stream()
                .allMatch(item -> item.getStatusItem() == StatusPedidoItem.CANCELADO || item.getStatusItem() == StatusPedidoItem.SEM_ESTOQUE);

        if (todosCancelados) {
            this.cancelar("Itens Cancelados ou Sem Estoque");
        }
    }

    public void cancelar(String motivoCancelamento) {
        alterarStatus(StatusPedido.CANCELADO);
        this.motivoCancelamento = motivoCancelamento;
    }

    public void alterarStatus(StatusPedido novoStatus) {

        if (!this.status.podeIrPara(novoStatus)) {
            throw new IllegalStateException(
                    "Pedido %s, Transição inválida de %s para %s"
                            .formatted(this.id, this.status, novoStatus)
            );
        }

        this.status = novoStatus;
    }

    public void pedidoReservado(){
       this.alterarStatus(StatusPedido.RESERVADO_ESTOQUE);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }
}
