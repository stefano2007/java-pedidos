package com.stefano.pedidos.kafka.consumer;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.repository.PedidoRepository;
import com.stefano.pedidos.kafka.constants.KafkaTopics;
import com.stefano.pedidos.kafka.event.PedidoEvent;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CancelamentoWorker {

    private static final Logger logger = LoggerFactory.getLogger(CancelamentoWorker.class);
    private final PedidoRepository pedidoRepository;

    public CancelamentoWorker(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @KafkaListener(topics = KafkaTopics.CANCELADO)
    @Transactional
    public void consumirCancelado(PedidoEvent event) {

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        logger.info("Pedido cancelado: {}", pedido.getId());
    }

}
