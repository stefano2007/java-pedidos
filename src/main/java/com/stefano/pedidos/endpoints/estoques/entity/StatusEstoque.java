package com.stefano.pedidos.endpoints.estoques.entity;

import java.util.Arrays;
import java.util.Set;

public enum StatusEstoque {
    CRIADO {
        @Override
        public Set<StatusEstoque> proximos() {
            return Set.of(CONFERIDO, REJEITADO);
        }
    },
    CONFERIDO {
        @Override
        public Set<StatusEstoque> proximos() {
            return Set.of();
        }
    },
    REJEITADO {
        @Override
        public Set<StatusEstoque> proximos() {
            return Set.of();
        }
    };

    public abstract Set<StatusEstoque> proximos();

    public boolean podeIrPara(StatusEstoque destino) {
        return proximos().contains(destino);
    }

    public static StatusEstoque from(String value) {

        if (value == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }

        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Status inválido: " + value));
    }
}
