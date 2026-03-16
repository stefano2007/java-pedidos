package com.stefano.pedidos.endpoints.estoques.dto.request;

import com.stefano.pedidos.endpoints.estoques.entity.TipoMovimentacaoEstoque;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record EstoqueRequest(

        @NotNull(message = "Usuário é obrigatório")
        @Positive(message = "Usuário inválido")
        Long usuarioId,

        @NotEmpty(message = "O produto estoque deve possuir ao menos um item")
        @Valid
        List<ProdutoEstoqueItemRequest> itens
) {
    public record ProdutoEstoqueItemRequest(
            @NotNull(message = "Produto é obrigatório")
            @Positive(message = "Produto inválido")
            Long produtoId,

            @NotNull(message = "Quantidade é obrigatório")
            @Positive(message = "Quantidade inválido")
            Integer quantidade,

            @NotNull(message = "Tipo de movimentação é obrigatório")
            TipoMovimentacaoEstoque tipo
    ) {
    }
}
