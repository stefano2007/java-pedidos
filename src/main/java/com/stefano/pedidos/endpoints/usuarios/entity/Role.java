package com.stefano.pedidos.endpoints.usuarios.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome; // ROLE_ADMIN, ROLE_USER, ROLE_GERENCIADOR

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToMany(mappedBy = "roles")
    private List<Usuario> usuarios;

    protected Role() {}

    public Role(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters...
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public List<Usuario> getUsuarios() { return usuarios; }
}