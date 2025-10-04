package br.com.fintech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Investimento extends Transacao {
    private String nome;
    private LocalDate dataRealizacao;
    private LocalDate dataVencimento;

    //vai virar uma classe !
    private Long instituicaoId;

    public Investimento() {}

    public Investimento(Long id, Long usuarioId, String descricao, Long categoriaId, BigDecimal valor, String nome, LocalDate dataRealizacao, LocalDate dataVencimento, Long instituicaoId) {
        super(id, usuarioId, descricao, categoriaId, valor);
        this.nome = nome;
        this.dataRealizacao = dataRealizacao;
        this.dataVencimento = dataVencimento;
        this.instituicaoId = instituicaoId;
    }

    public String getNome() {
        return this.nome;
    }

    public LocalDate getDataRealizacao() {
        return this.dataRealizacao;
    }

    public LocalDate getDataVencimento() {
        return this.dataVencimento;
    }

    public Long getInstituicaoId() { return this.instituicaoId; }

    public void setNome(String nome) { this.nome = nome; }

    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }

    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public void setInstituicaoId(Long instituicaoId) { this.instituicaoId = instituicaoId; }

    @Override

    public String getTipoMovimentacao() {
        return "Investimento";
    }

    public int getMultiplicador() {
        return -1;
    }

    public String toString() {
        return "Investimento{" +
                super.toString().substring(super.toString().indexOf("{") + 1, super.toString().length() - 1) +
                ", instituicao='" + this.instituicaoId + '\'' +
                ", nome='" + this.nome + '\'' +
                ", dataRealizacao=" + this.dataRealizacao +
                ", dataVencimento=" + this.dataVencimento +
                '}';
    }
}