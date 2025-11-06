package br.com.fintech.dto;

import br.com.fintech.model.TipoRecebimento;

public class TipoRecebimentoDTO {
    private Long id;
    private String descricao;

    public TipoRecebimentoDTO(TipoRecebimento tipo) {
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