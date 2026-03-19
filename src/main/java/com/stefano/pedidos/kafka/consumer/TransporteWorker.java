package com.stefano.pedidos.kafka.consumer;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.repository.PedidoRepository;
import com.stefano.pedidos.kafka.constants.KafkaTopics;
import com.stefano.pedidos.kafka.event.PedidoEvent;
import com.stefano.pedidos.kafka.producer.PedidoProducer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransporteWorker {

    private static final Logger logger = LoggerFactory.getLogger(TransporteWorker.class);
    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;

    public TransporteWorker(PedidoRepository pedidoRepository, PedidoProducer pedidoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoProducer = pedidoProducer;
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_SEPARADO)
    @Transactional
    public void consumirSeparacao(PedidoEvent event) {

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        pedido.alterarStatusEmTransporte();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", KafkaTopics.PEDIDO_SEPARADO, pedido.getId(), pedido.getStatus());
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_EM_TRANSPORTE)
    @Transactional
    public void consumirEmTransporte(PedidoEvent event) {

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        pedido.alterarStatusEntregue();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", KafkaTopics.PEDIDO_EM_TRANSPORTE, pedido.getId(), pedido.getStatus());
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_ENTREGUE)
    @Transactional
    public void consumirEntregue(PedidoEvent event) {

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        // fazer algo aqui

        logger.info("Pedido entregue: {}", pedido.getId());
    }
}
