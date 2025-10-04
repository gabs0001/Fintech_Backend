package br.com.fintech.model;

public class Instituicao {
    private Long id;
    private String nome;

    public Instituicao() {}

    public Instituicao(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return this.id; }

    public String getNome() { return this.nome; }

    public void setId(Long id) { this.id = id; }

    public void setNome(String nome) { this.nome = nome; }
}
