package br.com.fintech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Investimento extends Transacao {
    private String nome;
    private LocalDate dataRealizacao;
    private LocalDate dataVencimento;
    private Instituicao instituicao;

    public Investimento() {}

    public Investimento(Long id, Long usuarioId, String descricao, Categoria categoria, BigDecimal valor, String nome, LocalDate dataRealizacao, LocalDate dataVencimento, Instituicao instituicao) {
        super(id, usuarioId, descricao, categoria, valor);
        this.nome = nome;
        this.dataRealizacao = dataRealizacao;
        this.dataVencimento = dataVencimento;
        this.instituicao = instituicao;
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

    public Instituicao getInstituicao() { return this.instituicao; }

    public Long getInstituicaoId() {
        return this.instituicao != null ? this.instituicao.getId() : null;
    }

    public void setNome(String nome) { this.nome = nome; }

    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }

    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public void setInstituicao(Instituicao instituicao) { this.instituicao = instituicao; }

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
                ", instituicao='" + (instituicao != null ? instituicao.getNome() : "N/A") + '\'' +
                ", nome='" + this.nome + '\'' +
                ", dataRealizacao=" + this.dataRealizacao +
                ", dataVencimento=" + this.dataVencimento +
                '}';
    }
}