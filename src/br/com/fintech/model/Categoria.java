package br.com.fintech.model;

public class Categoria {
    private Long id;
    private String descricao;

    public Categoria() { }

    public Categoria(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Long getId() { return this.id; }

    public String getDescricao() { return this.descricao; }

    public void setId(Long id) { this.id = id; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
}