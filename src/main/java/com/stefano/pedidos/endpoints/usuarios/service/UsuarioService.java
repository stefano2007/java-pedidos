package com.stefano.pedidos.endpoints.usuarios.service;

import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import com.stefano.pedidos.endpoints.usuarios.exception.SenhaInvalidaException;
import com.stefano.pedidos.endpoints.usuarios.exception.UsuarioJaExisteException;
import com.stefano.pedidos.endpoints.usuarios.dto.request.UsuarioRequest;
import com.stefano.pedidos.endpoints.usuarios.dto.response.UsuarioResponse;
import com.stefano.pedidos.endpoints.usuarios.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse criar(UsuarioRequest request) {
        if (!request.senha().equals(request.confirmacaoSenha())) {
            throw new SenhaInvalidaException("Senha de confirmação diferente da senha informada");
        }

        if (this.obterPorEmail(request.email()).isPresent()) {
            throw new UsuarioJaExisteException("Usuário já existe");
        }

        Usuario novoUsuario = Usuario.criarUsuario(request.nome(), request.email(), request.senha());
        usuarioRepository.save(novoUsuario);

        return UsuarioResponse.of(novoUsuario);
    }

    public Page<UsuarioResponse> obterTodos(Pageable pageable) {
        return usuarioRepository
                .findAll(pageable)
                .map(UsuarioResponse::of);
    }

    public UsuarioResponse obterPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(UsuarioResponse::of)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    private Optional<Usuario> obterPorEmail(String email){
        return usuarioRepository.findByEmail(email);
    }
}
