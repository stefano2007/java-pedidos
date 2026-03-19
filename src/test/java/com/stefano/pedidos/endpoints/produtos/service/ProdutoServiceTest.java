package com.stefano.pedidos.endpoints.produtos.service;

import com.stefano.pedidos.endpoints.produtos.dto.request.ProdutoRequest;
import com.stefano.pedidos.endpoints.produtos.dto.response.ProdutoResponse;
import com.stefano.pedidos.endpoints.produtos.entity.Produto;
import com.stefano.pedidos.endpoints.produtos.repository.ProdutoRepository;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("ProdutoService - Testes Unitários")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void preparar() {
        MockitoAnnotations.openMocks(this);
        produto = mock(Produto.class);
    }

    @Test
    @DisplayName("Deve obter todos os produtos com paginação")
    void deveObterTodosProdutosComPaginacao() {
        // Arrange
        Page<Produto> paginaEsperada = new PageImpl<>(List.of(produto));
        when(produtoRepository.findAll(any(Pageable.class)))
            .thenReturn(paginaEsperada);

        // Act
        Page<ProdutoResponse> resultado = produtoService.obterTodos(mock(Pageable.class));

        // Assert
        assertThat(resultado).isNotEmpty();
        verify(produtoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve obter produto por ID")
    void deveObterProdutoPorId() {
        // Arrange
        Long produtoId = 1L;
        when(produtoRepository.findById(produtoId))
            .thenReturn(Optional.of(produto));

        // Act
        ProdutoResponse resultado = produtoService.obterPorId(produtoId);

        // Assert
        assertThat(resultado).isNotNull();
        verify(produtoRepository, times(1)).findById(produtoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        Long produtoId = 999L;
        when(produtoRepository.findById(produtoId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.obterPorId(produtoId))
            .isInstanceOf(RecursoNaoEncontradoException.class)
            .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("Deve criar novo produto")
    void deveCriarNovoProduto() {
        // Arrange
        ProdutoRequest requisicao = new ProdutoRequest(
            "Notebook Dell",
            "Notebook 15 polegadas",
            new BigDecimal("3500.00")
        );

        when(produtoRepository.save(any(Produto.class)))
            .thenReturn(produto);

        // Act
        ProdutoResponse resultado = produtoService.criar(requisicao);

        // Assert
        assertThat(resultado).isNotNull();
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve inativar produto")
    void deveInativarProduto() {
        // Arrange
        Long produtoId = 1L;
        when(produtoRepository.findById(produtoId))
            .thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class)))
            .thenReturn(produto);

        // Act
        produtoService.inativarProduto(produtoId);

        // Assert
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve retornar Optional com produto")
    void deveRetornarOptionalComProduto() {
        // Arrange
        Long produtoId = 1L;
        when(produtoRepository.findById(produtoId))
            .thenReturn(Optional.of(produto));

        // Act
        Optional<Produto> resultado = produtoService.obterProduto(produtoId);

        // Assert
        assertThat(resultado)
            .isPresent()
            .contains(produto);
    }

    @Test
    @DisplayName("Deve retornar empty quando produto não existe")
    void deveRetornarEmptyQuandoProdutoNaoExiste() {
        // Arrange
        Long produtoId = 999L;
        when(produtoRepository.findById(produtoId))
            .thenReturn(Optional.empty());

        // Act
        Optional<Produto> resultado = produtoService.obterProduto(produtoId);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
