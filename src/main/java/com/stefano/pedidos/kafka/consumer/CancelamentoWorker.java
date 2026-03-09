package com.stefano.pedidos.kafka.consumer;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.repository.PedidoRepository;
import com.stefano.pedidos.kafka.constants.KafkaTopics;
import com.stefano.pedidos.kafka.event.PedidoEvent;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CancelamentoWorker {

    private final PedidoRepository pedidoRepository;

    public CancelamentoWorker(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @KafkaListener(topics = KafkaTopics.CANCELADO)
    @Transactional
    public void consumirCancelado(PedidoEvent event) {

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow();

        //Fazer algo
        System.out.println("*** Cancelado Pedido :  " + pedido.getId());
    }

}
