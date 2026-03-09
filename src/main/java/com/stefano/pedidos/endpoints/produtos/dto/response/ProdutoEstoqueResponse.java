package com.stefano.pedidos.endpoints.produtos.dto.response;

import com.stefano.pedidos.endpoints.produtos.entity.TipoMovimentacaoEstoque;
import com.stefano.pedidos.endpoints.produtos.entity.ProdutoEstoque;
import com.stefano.pedidos.endpoints.produtos.entity.StatusEstoque;
import com.stefano.pedidos.endpoints.usuarios.dto.response.UsuarioResponse;

import java.time.LocalDateTime;
import java.util.Optional;

public record ProdutoEstoqueResponse(
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
    public static ProdutoEstoqueResponse of(ProdutoEstoque produtoEstoque) {
        UsuarioResponse usuarioResponse =
                Optional.ofNullable(produtoEstoque.getUsuario())
                        .map(UsuarioResponse::of)
                        .orElse(null);

        UsuarioResponse usuarioConferenciaResponse =
                Optional.ofNullable(produtoEstoque.getUsuarioConferencia())
                        .map(UsuarioResponse::of)
                        .orElse(null);

        return new ProdutoEstoqueResponse(
                produtoEstoque.getId(), ProdutoResponse.of(produtoEstoque.getProduto()), produtoEstoque.getQuantidade(),
                produtoEstoque.getQuantidadeEstoque(), usuarioResponse, produtoEstoque.getStatusEstoque(),
                produtoEstoque.getDataCriacao(), usuarioConferenciaResponse, produtoEstoque.getTipo()
        );
    }
}
