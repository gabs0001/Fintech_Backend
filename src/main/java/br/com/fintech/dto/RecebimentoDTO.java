package br.com.fintech.dto;

import br.com.fintech.model.Recebimento;
import br.com.fintech.model.TipoRecebimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecebimentoDTO {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private String tipoRecebimento;

    public RecebimentoDTO(Recebimento recebimento) {
        this.id = recebimento.getId();
        this.descricao = recebimento.getDescricao();
        this.valor = recebimento.getValor();
        this.data = recebimento.getDataRecebimento();
        this.tipoRecebimento = recebimento.getTipoRecebimento() != null ?
                recebimento.getTipoRecebimento().getDescricao() : null;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public String getTipoRecebimento() {
        return tipoRecebimento;
    }
}