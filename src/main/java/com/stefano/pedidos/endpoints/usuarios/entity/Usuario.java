package com.stefano.pedidos.endpoints.usuarios.entity;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;

    // E adicione este campo na classe Usuario (após o campo 'ativo'):
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Adicione estes getters:
    public Set<Role> getRoles() {
        return roles;
    }

    protected Usuario() {}

    public static Usuario criarUsuario(String nome, String email, String senha, Role role){
        Usuario novoUsuario = new Usuario();
        novoUsuario.nome = nome;
        novoUsuario.email = email;
        novoUsuario.senha = senha;
        novoUsuario.roles.add(role);
        return novoUsuario;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }
}