package com.stefano.pedidos.endpoints.produtos.repository;

import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
