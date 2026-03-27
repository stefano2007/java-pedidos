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
        /**
         * Aqui é onde a lógica de separação do pedido deve ser implementada.
         * Por exemplo, comunicar o setor de separação para separar os itens do pedido, atualizar o status do pedido, etc.
         */
        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        esperaProgramadaEmSegundos(2); // Simula um tempo de processamento para separação do pedido

        pedido.alterarStatusEmSeparacao();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        criarLogSucesso(KafkaTopics.PEDIDO_RESERVADO_ESTOQUE, pedido);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_EM_SEPARACAO)
    @Transactional
    public void consumirEmSeparacao(PedidoEvent event) {
        /**
         * Aqui é onde a lógica de separação do pedido deve ser implementada.
         * Por exemplo, comunicar o setor de separação para separar os itens do pedido, atualizar o status do pedido, etc.
         */
        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        esperaProgramadaEmSegundos(1); // Simula um tempo de processamento para separação do pedido

        pedido.alterarStatusSeparado();

        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        criarLogSucesso(KafkaTopics.PEDIDO_EM_SEPARACAO, pedido);
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
