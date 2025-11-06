package br.com.fintech.dto;

import br.com.fintech.model.CategoriaGasto;

public class CategoriaGastoDTO {
    private Long id;
    private String descricao;

    public CategoriaGastoDTO(CategoriaGasto categoria) {
        this.id = categoria.getId();
        this.descricao = categoria.getDescricao();
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}