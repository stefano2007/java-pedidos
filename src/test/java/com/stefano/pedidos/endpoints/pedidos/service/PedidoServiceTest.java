package com.stefano.pedidos.endpoints.pedidos.service;

import com.stefano.pedidos.endpoints.estoques.service.EstoqueService;
import com.stefano.pedidos.endpoints.pedidos.dto.request.CancelarPedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.dto.request.PedidoRequest;
import com.stefano.pedidos.endpoints.pedidos.dto.response.PedidoResponse;
import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import com.stefano.pedidos.endpoints.pedidos.repository.PedidoRepository;
import com.stefano.pedidos.endpoints.produtos.service.ProdutoService;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.endpoints.usuarios.service.UsuarioService;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import com.stefano.pedidos.kafka.producer.PedidoProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("PedidoService - Testes Unitários")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private PedidoProducer pedidoProducer;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        MockitoAnnotations.openMocks(this);
        pedido = mock(Pedido.class);
        usuario = mock(Usuario.class);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não existe")
    void deveLancarExcecaoQuandoPedidoNaoExiste() {
        // Arrange
        Long pedidoId = 999L;
        when(pedidoRepository.findById(pedidoId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.obterPorId(pedidoId))
            .isInstanceOf(RecursoNaoEncontradoException.class)
            .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    @DisplayName("Deve retornar pedido para obterPedido")
    void deveRetornarPedidoParaObterPedido() {
        // Arrange
        Long pedidoId = 1L;
        when(pedidoRepository.findById(pedidoId))
            .thenReturn(Optional.of(pedido));

        // Act
        Optional<Pedido> resultado = pedidoService.obterPedido(pedidoId);

        // Assert
        assertThat(resultado)
            .isPresent()
            .contains(pedido);
    }

    @Test
    @DisplayName("Deve retornar empty para pedido não encontrado")
    void deveRetornarEmptyParaPedidoNaoEncontrado() {
        // Arrange
        Long pedidoId = 999L;
        when(pedidoRepository.findById(pedidoId))
            .thenReturn(Optional.empty());

        // Act
        Optional<Pedido> resultado = pedidoService.obterPedido(pedidoId);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
