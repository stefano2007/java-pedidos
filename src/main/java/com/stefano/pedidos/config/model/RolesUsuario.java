package com.stefano.pedidos.config.model;

//todo garantirar as roles existam no banco de dados, ou criar um enum para isso
public enum RolesUsuario {
    ADMIN("ROLE_ADMIN"),
    GERENCIADOR("ROLE_GERENCIADOR"),
    USER("ROLE_USER");

    private String nome;

    RolesUsuario(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}