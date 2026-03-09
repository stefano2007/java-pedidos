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

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoStatus> historicoStatus = new ArrayList<>();

    protected Pedido() {
    }

    public static Pedido criarPedido(Usuario usuario) {
        Pedido novoPedido = new Pedido();
        novoPedido.usuario = usuario;
        novoPedido.alterarStatus(StatusPedido.CRIADO);
        return novoPedido;
    }

    public void adicionarPedidoItens(List<PedidoItem> pedidoItens) {
        pedidoItens.forEach(this::adicionarItem);
    }

    private void adicionarItem(PedidoItem item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        this.itens.add(item);
        item.setPedido(this);
    }

    public void cancelarSeTodosItensCanceladosOuSemEstoque() {

        if (this.itens == null || this.itens.isEmpty()) {
            this.cancelar("Não existe itens para o pedido");
        }

        boolean todosCancelados = this.itens.stream()
                .allMatch(item -> item.getStatusItem() == StatusPedidoItem.CANCELADO || item.getStatusItem() == StatusPedidoItem.SEM_ESTOQUE);

        if (todosCancelados) {
            this.cancelar("Itens Cancelados ou Sem Estoque");
        }
    }

    public boolean criado() {
        return StatusPedido.CRIADO.equals(this.status);
    }

    public boolean validado() {
        return StatusPedido.VALIDADO.equals(this.status);
    }

    public void alterarStatusValidado() {
        this.alterarStatus(StatusPedido.VALIDADO);
        this.itens.forEach(PedidoItem::validarPeditoItem);
    }

    public boolean reservadoEstoque() {
        return StatusPedido.RESERVADO_ESTOQUE.equals(this.status);
    }

    public void alterarStatusReservadoEstoqueOuCancelar() {
        this.cancelarSeTodosItensCanceladosOuSemEstoque();
        if (!cancelado()) {
            this.alterarStatus(StatusPedido.RESERVADO_ESTOQUE);
        }
    }

    public boolean emSeperacao() {
        return StatusPedido.EM_SEPARACAO.equals(this.status);
    }

    public void alterarStatusEmSeparacao() {
        this.alterarStatus(StatusPedido.EM_SEPARACAO);
    }

    public boolean separado() {
        return StatusPedido.SEPARADO.equals(this.status);
    }

    public void alterarStatusSeparado() {
        this.alterarStatus(StatusPedido.SEPARADO);
    }

    public boolean emTransporte() {
        return StatusPedido.EM_TRANSPORTE.equals(this.status);
    }

    public void alterarStatusEmTransporte() {
        this.alterarStatus(StatusPedido.EM_TRANSPORTE);
    }

    public boolean entregue() {
        return StatusPedido.ENTREGUE.equals(this.status);
    }

    public void alterarStatusEntregue() {
        this.alterarStatus(StatusPedido.ENTREGUE);
    }

    public boolean cancelado() {
        return StatusPedido.CANCELADO.equals(this.status);
    }

    public void cancelar(String motivoCancelamento) {
        alterarStatus(StatusPedido.CANCELADO);
        this.motivoCancelamento = motivoCancelamento;
    }

    private void alterarStatus(StatusPedido novoStatus) {

        if (this.status != null && !this.status.podeIrPara(novoStatus)) {
            throw new IllegalStateException(
                    "Pedido %s, Transição inválida de %s para %s"
                            .formatted(this.id, this.status, novoStatus)
            );
        }

        this.status = novoStatus;
        this.adicionarHistoricoStatusPedido();
    }

    private void adicionarHistoricoStatusPedido() {
        if (this.historicoStatus == null) {
            this.historicoStatus = new ArrayList<>();
        }
        this.historicoStatus.add(PedidoStatus.criarPedidoStatus(this));
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

    public List<PedidoStatus> getHistoricoStatus() {
        return historicoStatus;
    }


}
