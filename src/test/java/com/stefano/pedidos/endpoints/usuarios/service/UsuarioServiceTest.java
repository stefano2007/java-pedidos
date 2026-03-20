package com.stefano.pedidos.endpoints.usuarios.service;

import com.stefano.pedidos.config.model.RolesUsuario;
import com.stefano.pedidos.endpoints.usuarios.dto.request.UsuarioRequest;
import com.stefano.pedidos.endpoints.usuarios.dto.response.UsuarioResponse;
import com.stefano.pedidos.endpoints.usuarios.entity.Role;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.endpoints.usuarios.exception.SenhaInvalidaException;
import com.stefano.pedidos.endpoints.usuarios.exception.UsuarioJaExisteException;
import com.stefano.pedidos.endpoints.usuarios.repository.UsuarioRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private RoleService roleService;

    private Usuario usuario;

    @BeforeEach
    void preparar() {
        MockitoAnnotations.openMocks(this);
        usuario = mock(Usuario.class);

        when(roleService.obterRolePorNome(RolesUsuario.USER.getNome()))
                .thenReturn(new Role(RolesUsuario.USER.getNome(), "Role de usuário comum"));
    }

    @Test
    @DisplayName("Deve criar usuário com dados válidos")
    void deveCriarUsuarioComDadosValidos() {
        // Arrange
        UsuarioRequest requisicao = new UsuarioRequest(
            "João Silva",
            "joao@test.com",
            "senha123456",
            "senha123456"
        );

        when(usuarioRepository.findByEmail("joao@test.com"))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123456"))
            .thenReturn("senha_criptografada");
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);



        // Act
        UsuarioResponse resultado = usuarioService.criar(requisicao);

        // Assert
        assertThat(resultado).isNotNull();
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode("senha123456");
    }

    @Test
    @DisplayName("Deve lançar exceção quando senhas não conferem")
    void deveLancarExcecaoQuandoSenhasNaoConferem() {
        // Arrange
        UsuarioRequest requisicao = new UsuarioRequest(
            "João Silva",
            "joao@test.com",
            "senha123456",
            "senha_diferente"
        );

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criar(requisicao))
            .isInstanceOf(SenhaInvalidaException.class)
            .hasMessageContaining("Senha de confirmação diferente");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário já existe")
    void deveLancarExcecaoQuandoUsuarioJaExiste() {
        // Arrange
        UsuarioRequest requisicao = new UsuarioRequest(
            "João Silva",
            "joao@test.com",
            "senha123456",
            "senha123456"
        );

        when(usuarioRepository.findByEmail("joao@test.com"))
            .thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.criar(requisicao))
            .isInstanceOf(UsuarioJaExisteException.class)
            .hasMessageContaining("Usuário já existe");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve obter todos os usuários com paginação")
    void deveObterTodosUsuariosComPaginacao() {
        // Arrange
        Page<Usuario> paginaEsperada = new PageImpl<>(List.of(usuario));
        when(usuarioRepository.findAll(any(Pageable.class)))
            .thenReturn(paginaEsperada);

        // Act
        Page<UsuarioResponse> resultado = usuarioService.obterTodos(mock(Pageable.class));

        // Assert
        assertThat(resultado).isNotEmpty();
        verify(usuarioRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve obter usuário por ID")
    void deveObterUsuarioPorId() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioRepository.findById(usuarioId))
            .thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponse resultado = usuarioService.obterPorId(usuarioId);

        // Assert
        assertThat(resultado).isNotNull();
        verify(usuarioRepository, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        Long usuarioId = 999L;
        when(usuarioRepository.findById(usuarioId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.obterPorId(usuarioId))
            .isInstanceOf(RecursoNaoEncontradoException.class)
            .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve criptografar senha ao criar usuário")
    void deveCriptografarSenhaAoCriarUsuario() {
        // Arrange
        UsuarioRequest requisicao = new UsuarioRequest(
            "João Silva",
            "joao@test.com",
            "senha123456",
            "senha123456"
        );

        when(usuarioRepository.findByEmail("joao@test.com"))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123456"))
            .thenReturn("$2a$10$encrypted_password");
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        // Act
        usuarioService.criar(requisicao);

        // Assert
        verify(passwordEncoder, times(1)).encode("senha123456");
    }
}

