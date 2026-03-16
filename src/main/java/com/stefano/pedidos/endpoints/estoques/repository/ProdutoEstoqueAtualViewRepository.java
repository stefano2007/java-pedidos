package com.stefano.pedidos.endpoints.estoques.repository;

import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoqueAtualView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoEstoqueAtualViewRepository extends JpaRepository<ProdutoEstoqueAtualView, Long> {
}
