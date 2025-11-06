package br.com.fintech.dto;

import br.com.fintech.model.TipoInvestimento;

public class TipoInvestimentoDTO {
    private Long id;
    private String descricao;

    public TipoInvestimentoDTO(TipoInvestimento tipo) {
        this.id = tipo.getId();
        this.descricao = tipo.getDescricao();
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}