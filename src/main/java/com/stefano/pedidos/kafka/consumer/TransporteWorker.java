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
        /**
         * Aqui é onde a lógica de transporte do pedido deve ser implementada.
         * Por exemplo, comunicar o setor de logística para realizar a entrega, calcular o prazo de entrega, etc.
         */
        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        esperaProgramadaEmSegundos(2);

        pedido.alterarStatusEmTransporte();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        criarLogSucesso(KafkaTopics.PEDIDO_SEPARADO, pedido);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_EM_TRANSPORTE)
    @Transactional
    public void consumirEmTransporte(PedidoEvent event) {
        /**
        * Aqui é onde a lógica de transporte do pedido deve ser implementada.
        * Por exemplo, comunicar a transportadora, atualizar o status do pedido, etc.
        */
        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        esperaProgramadaEmSegundos(3);

        pedido.alterarStatusEntregue();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        criarLogSucesso(KafkaTopics.PEDIDO_EM_TRANSPORTE, pedido);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_ENTREGUE)
    @Transactional
    public void consumirEntregue(PedidoEvent event) {
        /**
         * Aqui é onde a lógica de pós-entrega do pedido deve ser implementada.
         * Por exemplo, comunicar o cliente sobre a entrega, solicitar feedback, etc.
         */

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        esperaProgramadaEmSegundos(1);

        logger.info("Pedido entregue: {}", pedido.getId());
    }

    private void criarLogSucesso(String topicoAtual, Pedido pedido) {
        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", topicoAtual, pedido.getId(), pedido.getStatus());
    }

    private void esperaProgramadaEmSegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
