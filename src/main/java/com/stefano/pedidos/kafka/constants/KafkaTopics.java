package com.stefano.pedidos.kafka.constants;

public final class KafkaTopics {
    private KafkaTopics() {
    }

    public static final String PEDIDO_CRIADO = "pedido-criado";
    public static final String PEDIDO_VALIDADO = "pedido-validado";
    public static final String PEDIDO_RESERVADO_ESTOQUE = "pedido-reservado-estoque";
    public static final String PEDIDO_EM_SEPARACAO = "pedido-em-separacao";
    public static final String PEDIDO_SEPARADO = "pedido-separado";
    public static final String PEDIDO_EM_TRANSPORTE = "pedido-em-transporte";
    public static final String PEDIDO_ENTREGUE = "pedido-entrege";
    public static final String CANCELADO = "pedido-cancelado";

}
