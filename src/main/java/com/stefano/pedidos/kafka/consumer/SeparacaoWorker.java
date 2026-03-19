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
public class SeparacaoWorker {

    private static final Logger logger = LoggerFactory.getLogger(SeparacaoWorker.class);
    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;

    public SeparacaoWorker(PedidoRepository pedidoRepository, PedidoProducer pedidoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoProducer = pedidoProducer;
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_RESERVADO_ESTOQUE)
    @Transactional
    public void consumir(PedidoEvent event) {

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        pedido.alterarStatusEmSeparacao();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", KafkaTopics.PEDIDO_RESERVADO_ESTOQUE, pedido.getId(), pedido.getStatus());
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_EM_SEPARACAO)
    @Transactional
    public void consumirEmSeparacao(PedidoEvent event) {

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        pedido.alterarStatusSeparado();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", KafkaTopics.PEDIDO_EM_SEPARACAO, pedido.getId(), pedido.getStatus());
    }
}
