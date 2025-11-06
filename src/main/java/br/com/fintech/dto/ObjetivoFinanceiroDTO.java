package br.com.fintech.dto;

import br.com.fintech.model.ObjetivoFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ObjetivoFinanceiroDTO {
    private Long id;
    private Long usuarioId;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataConclusao;

    public ObjetivoFinanceiroDTO(ObjetivoFinanceiro objetivo) {
        this.id = objetivo.getId();
        this.usuarioId = objetivo.getUsuarioId();
        this.nome = objetivo.getNome();
        this.descricao = objetivo.getDescricao();
        this.valor = objetivo.getValor();
        this.dataConclusao = objetivo.getDataConclusao();
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }
}