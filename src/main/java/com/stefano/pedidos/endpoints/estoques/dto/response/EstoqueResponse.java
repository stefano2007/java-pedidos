package com.stefano.pedidos.endpoints.estoques.dto.response;

import com.stefano.pedidos.endpoints.estoques.entity.ProdutoEstoque;
import com.stefano.pedidos.endpoints.estoques.entity.StatusEstoque;
import com.stefano.pedidos.endpoints.estoques.entity.TipoMovimentacaoEstoque;
import com.stefano.pedidos.endpoints.produtos.dto.response.ProdutoResponse;
import com.stefano.pedidos.endpoints.usuarios.dto.response.UsuarioResponse;

import java.time.LocalDateTime;
import java.util.Optional;

public record EstoqueResponse(
        Long id,
        ProdutoResponse produto,
        Integer quantidade,
        Integer quantidadeEstoque,
        UsuarioResponse usuario,
        StatusEstoque statusEstoque,
        LocalDateTime dataCriacao,
        UsuarioResponse usuarioConferencia,
        TipoMovimentacaoEstoque tipo
) {
    public static EstoqueResponse of(ProdutoEstoque produtoEstoque) {
        UsuarioResponse usuarioResponse =
                Optional.ofNullable(produtoEstoque.getUsuario())
                        .map(UsuarioResponse::of)
                        .orElse(null);

        UsuarioResponse usuarioConferenciaResponse =
                Optional.ofNullable(produtoEstoque.getUsuarioConferencia())
                        .map(UsuarioResponse::of)
                        .orElse(null);

        return new EstoqueResponse(
                produtoEstoque.getId(), ProdutoResponse.of(produtoEstoque.getProduto()), produtoEstoque.getQuantidade(),
                produtoEstoque.getQuantidadeEstoque(), usuarioResponse, produtoEstoque.getStatusEstoque(),
                produtoEstoque.getDataCriacao(), usuarioConferenciaResponse, produtoEstoque.getTipo()
        );
    }
}
