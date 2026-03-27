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
public class ValidaPedidoWorker {

    private static final Logger logger = LoggerFactory.getLogger(ValidaPedidoWorker.class);
    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;

    public ValidaPedidoWorker(PedidoRepository pedidoRepository, PedidoProducer pedidoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoProducer = pedidoProducer;
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_CRIADO)
    @Transactional
    public void consumir(PedidoEvent event) {
        /**
         * Aqui é onde a lógica de validação do pedido deve ser implementada.
         * Por exemplo, você pode verificar se os dados do pedido estão completos, se os produtos existem, se as quantidades são válidas, etc.
         * Como exemplo validar se o cliente precisa ir para Analise de Credito manual ou se o pedido pode ser validado automaticamente.
         */

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado. PedidoId: " + event.pedidoId()));

        esperaProgramadaEmSegundos(3); // Simula um tempo de processamento para validação do pedido

        pedido.alterarStatusValidado();

        this.pedidoRepository.save(pedido);
        this.pedidoProducer.publicar(pedido);

        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", KafkaTopics.PEDIDO_CRIADO, pedido.getId(), pedido.getStatus());
    }

    private void esperaProgramadaEmSegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
