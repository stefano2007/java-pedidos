package com.stefano.pedidos.endpoints.estoques.repository;

import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoEstoqueRepository extends JpaRepository<ProdutoEstoque, Long> {
    List<ProdutoEstoque> findByProdutoId(Long produtoId);
}
