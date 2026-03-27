package com.stefano.pedidos.kafka.consumer;

import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoqueAtualView;
import com.stefano.pedidos.endpoints.estoques.service.EstoqueService;
import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.entity.PedidoItem;
import com.stefano.pedidos.endpoints.pedidos.entity.StatusPedidoItem;
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
public class ReservaEstoqueWorker {

    private static final Logger logger = LoggerFactory.getLogger(ReservaEstoqueWorker.class);
    private final PedidoRepository pedidoRepository;
    private final EstoqueService estoqueService;
    private final PedidoProducer pedidoProducer;

    public ReservaEstoqueWorker(PedidoRepository pedidoRepository, EstoqueService estoqueService, PedidoProducer pedidoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.estoqueService = estoqueService;
        this.pedidoProducer = pedidoProducer;
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_VALIDADO)
    @Transactional
    public void consumirPedidoValido(PedidoEvent event) {
        /**
         * Aqui é onde a lógica de reserva de estoque deve ser implementada.
         * Para cada item do pedido, verificar o estoque disponível e reservar a quantidade necessária ou a quantidade que resta no estoque.
         * Se o estoque for insuficiente para todos os itens, o pedido deve ser cancelado ou colocado em espera, dependendo da regra de negócio definida.
         */
        Pedido pedido = pedidoRepository.findById(event.pedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado. PedidoId: " + event.pedidoId()));

        for (PedidoItem item : pedido.getItens().stream().filter(i -> i.getStatusItem() == StatusPedidoItem.VALIDADO).toList()) {
            final int estoqueAtual = obterQuantidadeEstoqueAtual(item.getProduto().getId());

            final int quantidadeAtendida = estoqueDisponivelParaReserva(estoqueAtual, item.getQuantidade());

            if (quantidadeAtendida > 0) {
                item.reservarEstoque(quantidadeAtendida);
            } else {
                item.semEstoque("Estoque insuficiente");
            }
        }

        pedido.alterarStatusReservadoEstoqueOuCancelar();
        pedidoRepository.save(pedido);
        pedidoProducer.publicar(pedido);

        logger.info("Tópico processado: {}, PedidoId: {}, Status atual: {}", KafkaTopics.PEDIDO_VALIDADO, pedido.getId(), pedido.getStatus());
    }

    private int obterQuantidadeEstoqueAtual(Long produtoId) {
        return estoqueService.obterProdutoEstoqueAtualView(produtoId)
                .map(ProdutoEstoqueAtualView::getQuantidadeEstoque)
                .orElse(0);
    }

    private int estoqueDisponivelParaReserva(int estoqueAtual, int quantidadeSolicitada) {
        // Retorna a quantidade que pode ser reservada, que é o mínimo entre o estoque atual e a quantidade solicitada, mas nunca negativa
        return Math.max(0, Math.min(estoqueAtual, quantidadeSolicitada));
    }
}