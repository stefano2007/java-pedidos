package com.stefano.pedidos.kafka.producer;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.kafka.constants.KafkaTopics;
import com.stefano.pedidos.kafka.event.PedidoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoProducer {

    private final KafkaTemplate<String, PedidoEvent> kafkaTemplate;

    public PedidoProducer(KafkaTemplate<String, PedidoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(Pedido pedido) {

        String topico = escolherTopico(pedido);

        if (topico == null) {
            //todo: log de warning com informações do status atual
            return;
        }

        PedidoEvent event = new PedidoEvent(pedido.getId());

        kafkaTemplate.send(
                topico,
                event.pedidoId().toString(),
                event
        );
    }

    private String escolherTopico(Pedido pedido) {
        return switch (pedido.getStatus()) {
            case VALIDADO -> KafkaTopics.PEDIDO_VALIDADO;
            case RESERVADO_ESTOQUE -> KafkaTopics.PEDIDO_RESERVADO_ESTOQUE;
            case EM_SEPARACAO -> KafkaTopics.PEDIDO_EM_SEPARACAO;
            case SEPARADO -> KafkaTopics.PEDIDO_SEPARADO;
            case EM_TRANSPORTE -> KafkaTopics.PEDIDO_EM_TRANSPORTE;
            case ENTREGUE -> KafkaTopics.PEDIDO_ENTREGUE;
            case CANCELADO -> KafkaTopics.CANCELADO;
            default -> null;
        };
    }
}