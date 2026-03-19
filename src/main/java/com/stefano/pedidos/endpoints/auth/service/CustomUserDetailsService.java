package com.stefano.pedidos.endpoints.auth.service;

import com.stefano.pedidos.config.model.UserPrincipal;
import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import com.stefano.pedidos.endpoints.usuarios.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado: %s".formatted(email)));

        List<GrantedAuthority> rolesUsuario = usuario.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getNome()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenha(),
                rolesUsuario
        );
    }
}
