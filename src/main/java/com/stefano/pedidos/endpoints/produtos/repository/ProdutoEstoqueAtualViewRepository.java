package com.stefano.pedidos.endpoints.produtos.repository;

import com.stefano.pedidos.endpoints.produtos.entity.ProdutoEstoqueAtualView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoEstoqueAtualViewRepository extends JpaRepository<ProdutoEstoqueAtualView, Long> {
}
