package br.com.fintech.dto;

import br.com.fintech.model.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoDTO {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private String categoria;

    public GastoDTO(Gasto gasto) {
        this.id = gasto.getId();
        this.descricao = gasto.getDescricao();
        this.valor = gasto.getValor();
        this.data = gasto.getDataGasto();
        this.categoria = gasto.getCategoriaGasto() != null ? gasto.getCategoriaGasto().getDescricao() : null;
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

    public String getCategoria() {
        return categoria;
    }
}