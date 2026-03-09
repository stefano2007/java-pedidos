package com.stefano.pedidos.endpoints.usuarios.entity;

import com.stefano.pedidos.endpoints.pedidos.entity.Pedido;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    protected Usuario() {}

    public static Usuario criarUsuario(String nome, String email, String senha){
        Usuario novoUsuario = new Usuario();
        novoUsuario.nome = nome;
        novoUsuario.email = email;
        novoUsuario.senha = senha;
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