package com.stefano.pedidos.endpoints.usuarios.repository;

import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}