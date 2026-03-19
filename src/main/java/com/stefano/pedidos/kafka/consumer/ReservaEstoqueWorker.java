package com.stefano.pedidos.kafka.consumer;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.service.PedidoService;
import com.stefano.pedidos.kafka.constants.KafkaTopics;
import com.stefano.pedidos.kafka.event.PedidoEvent;
import com.stefano.pedidos.kafka.producer.PedidoProducer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReservaEstoqueWorker {

    private static final Logger logger = LoggerFactory.getLogger(ReservaEstoqueWorker.class);
    private final PedidoService pedidoService;
    private final PedidoProducer pedidoProducer;

    public ReservaEstoqueWorker(PedidoService pedidoService, PedidoProducer pedidoProducer) {
        this.pedidoService = pedidoService;
        this.pedidoProducer = pedidoProducer;
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_VALIDADO)
    @Transactional
    public void consumir(PedidoEvent event) {
        Pedido pedido = pedidoService.reservarEstoquePedido(event.pedidoId());

        pedidoProducer.publicar(pedido);

        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", KafkaTopics.PEDIDO_VALIDADO, pedido.getId(), pedido.getStatus());
    }
}