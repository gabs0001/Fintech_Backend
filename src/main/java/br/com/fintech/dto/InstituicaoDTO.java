package br.com.fintech.dto;

import br.com.fintech.model.Instituicao;

public class InstituicaoDTO {
    private Long id;
    private String nome;

    public InstituicaoDTO(Instituicao instituicao) {
        this.id = instituicao.getId();
        this.nome = instituicao.getNome();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}