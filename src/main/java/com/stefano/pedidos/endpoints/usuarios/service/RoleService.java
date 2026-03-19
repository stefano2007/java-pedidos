package com.stefano.pedidos.endpoints.usuarios.service;

import com.stefano.pedidos.endpoints.usuarios.entity.Role;
import com.stefano.pedidos.endpoints.usuarios.repository.RoleRepository;
import com.stefano.pedidos.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role criarRole(String nome, String descricao) {
        Role role = new Role(nome, descricao);
        return roleRepository.save(role);
    }

    public Role obterRolePorNome(String nome) {
        return roleRepository.findByNome(nome)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Role não encontrada: " + nome));
    }

    public List<Role> listarTodas() {
        return roleRepository.findAll();
    }

    public void deletarRole(Long id) {
        roleRepository.deleteById(id);
    }
}