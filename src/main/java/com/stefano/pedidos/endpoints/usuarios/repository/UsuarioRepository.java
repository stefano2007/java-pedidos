package com.stefano.pedidos.endpoints.usuarios.repository;

import com.stefano.pedidos.endpoints.usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//todo: adicionar cache para otimizar consulta por email, já que é utilizada no processo de autenticação
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}