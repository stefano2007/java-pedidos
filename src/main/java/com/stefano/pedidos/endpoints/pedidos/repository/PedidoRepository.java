package com.stefano.pedidos.endpoints.pedidos.repository;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
