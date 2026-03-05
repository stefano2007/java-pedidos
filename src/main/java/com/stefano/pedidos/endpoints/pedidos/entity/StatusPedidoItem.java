package com.stefano.pedidos.endpoints.pedidos.entity;

import java.util.Set;

public enum StatusPedidoItem {

    CRIADO {
        public Set<StatusPedidoItem> proximos() {
            return Set.of(VALIDADO, CANCELADO);
        }
    },

    VALIDADO {
        public Set<StatusPedidoItem> proximos() {
            return Set.of(RESERVADO_ESTOQUE, CANCELADO);
        }
    },

    RESERVADO_ESTOQUE {
        public Set<StatusPedidoItem> proximos() {
            return Set.of(CANCELADO);
        }
    },

    SEM_ESTOQUE {
        public Set<StatusPedidoItem> proximos() {
            return Set.of(CANCELADO);
        }
    },

    CANCELADO {
        public Set<StatusPedidoItem> proximos() {
            return Set.of();
        }
    };

    public abstract Set<StatusPedidoItem> proximos();

    public boolean podeIrPara(StatusPedidoItem destino) {
        return proximos().contains(destino);
    }
}