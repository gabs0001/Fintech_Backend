package br.com.fintech.dto;

import br.com.fintech.model.Usuario;

import java.time.LocalDate;

public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String genero;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.dataNascimento = usuario.getDataNascimento();
        this.genero = usuario.getGenero();
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }

    public String getEmail() { return email; }

    public LocalDate getDataNascimento() { return dataNascimento; }

    public String getGenero() { return genero; }
}
