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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    public UsuarioResponse criar(UsuarioRequest request) {
        if (!request.senha().equals(request.confirmacaoSenha())) {
            throw new SenhaInvalidaException("Senha de confirmação diferente da senha informada");
        }

        if (this.obterPorEmail(request.email()).isPresent()) {
            throw new UsuarioJaExisteException("Usuário já existe");
        }

        final String senhaCriptografada = passwordEncoder.encode(request.senha());

        //criar o usuario com usuario comum
        //Todo: da opção de criar um usuário com role admin, mas só pode ser feito por um admin
        Role roleUser = roleService.obterRolePorNome(RolesUsuario.USER.getNome());

        Usuario novoUsuario = Usuario.criarUsuario(request.nome(), request.email(), senhaCriptografada, roleUser);
        usuarioRepository.save(novoUsuario);

        return UsuarioResponse.of(novoUsuario);
    }

    public Page<UsuarioResponse> obterTodos(Pageable pageable) {
        return usuarioRepository
                .findAll(pageable)
                .map(UsuarioResponse::of);
    }

    public UsuarioResponse obterPorId(Long usuarioId) {
        return UsuarioResponse.of(obterUsuarioOuExcecao(usuarioId));
    }

    private Optional<Usuario> obterPorEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public Usuario obterUsuarioOuExcecao(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado: %s".formatted(usuarioId)));
    }
}
