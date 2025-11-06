package br.com.fintech.dto;

import br.com.fintech.model.Investimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestimentoDTO {
    private Long id;
    private String nomeAplicacao;
    private BigDecimal valorAplicacao;
    private String descricao;
    private LocalDate dataRealizacao;
    private LocalDate dataVencimento;
    private String tipoInvestimento;
    private String instituicao;

    public InvestimentoDTO(Investimento investimento) {
        this.id = investimento.getId();
        this.nomeAplicacao = investimento.getNome();
        this.valorAplicacao = investimento.getValor();
        this.descricao = investimento.getDescricao();
        this.dataRealizacao = investimento.getDataRealizacao();
        this.dataVencimento = investimento.getDataVencimento();
        this.tipoInvestimento = investimento.getTipoInvestimento() != null
                ? investimento.getTipoInvestimento().getDescricao()
                : null;
        this.instituicao = investimento.getInstituicao() != null
                ? investimento.getInstituicao().getNome()
                : null;
    }

    public Long getId() {
        return id;
    }

    public String getNomeAplicacao() {
        return nomeAplicacao;
    }

    public BigDecimal getValorAplicacao() {
        return valorAplicacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public String getTipoInvestimento() {
        return tipoInvestimento;
    }

    public String getInstituicao() {
        return instituicao;
    }
}
