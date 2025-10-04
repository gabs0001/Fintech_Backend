package br.com.fintech.model;

import java.time.LocalDate;

public class Usuario {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String genero;
    private String email;
    private String senha;

    public Usuario() {}

    public Usuario(Long id, String nome, LocalDate dataNascimento, String genero, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.email = email;
        this.senha = senha;
    }

    public Long getId() { return this.id; }

    public String getNome() { return this.nome; }

    public LocalDate getDataNascimento() { return this.dataNascimento; }

    public String getGenero() { return this.genero; }

    public String getEmail() { return this.email; }

    public String getSenha() { return this.senha; }

    public void setId(Long id) { this.id = id; }

    public void setNome(String nome) { this.nome = nome; }

    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public void setGenero(String genero) { this.genero = genero; }

    public void setEmail(String email) { this.email = email; }

    public void setSenha(String senha) { this.senha = senha; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + this.id +
                ", nome=" + this.nome +
                ", dataNascimento=" + this.dataNascimento +
                ", genero='" + this.genero + '\'' +
                ", email='" + this.email + '\'' +
                ", senha='" + this.senha + '\'' +
                '}';
    }
}