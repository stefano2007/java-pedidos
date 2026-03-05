package com.stefano.pedidos.endpoints.pedidos.entity;

import java.util.Set;

public enum StatusPedido {

    CRIADO {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(VALIDADO, CANCELADO);
        }
    },

    VALIDADO {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(RESERVADO_ESTOQUE, CANCELADO);
        }
    },

    RESERVADO_ESTOQUE {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(EM_SEPARACAO, CANCELADO);
        }
    },

    EM_SEPARACAO {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(SEPARADO, CANCELADO);
        }
    },

    SEPARADO {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(EM_TRANSPORTE, CANCELADO);
        }
    },

    EM_TRANSPORTE {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(ENTREGUE);
        }
    },

    ENTREGUE {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(); // estado final
        }
    },

    CANCELADO {
        @Override
        public Set<StatusPedido> proximos() {
            return Set.of(); // estado final
        }
    };

    public abstract Set<StatusPedido> proximos();

    public boolean podeIrPara(StatusPedido destino) {
        return proximos().contains(destino);
    }
}