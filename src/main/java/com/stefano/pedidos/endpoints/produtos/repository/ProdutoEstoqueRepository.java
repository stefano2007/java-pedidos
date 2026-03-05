package com.stefano.pedidos.endpoints.produtos.repository;

import com.stefano.pedidos.endpoints.produtos.entity.ProdutoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoEstoqueRepository extends JpaRepository<ProdutoEstoque, Long> {
    List<ProdutoEstoque> findByProdutoId(Long produtoId);
}
