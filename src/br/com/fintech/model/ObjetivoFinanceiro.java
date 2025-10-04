package br.com.fintech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ObjetivoFinanceiro {
    private Long id;
    private Long usuarioId;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataConclusao;

    public ObjetivoFinanceiro() { }

    public ObjetivoFinanceiro(Long id, Long usuarioId, String nome, String descricao, BigDecimal valor, LocalDate dataConclusao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.dataConclusao = dataConclusao;
    }

    public Long getId() { return this.id; }

    public Long getUsuarioId() { return this.usuarioId; }

    public String getNome() { return this.nome; }

    public String getDescricao() { return this.descricao; }

    public BigDecimal getValor() { return this.valor; }

    public LocalDate getDataConclusao() { return this.dataConclusao; }

    public void setId(Long id) { this.id = id; }

    public void setNome(String nome) { this.nome = nome; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

    public void setDataConclusao(LocalDate dataConclusao) { this.dataConclusao = dataConclusao; }

    @Override
    public String toString() {
        return "ObjetivoFinanceiro{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", dataConclusao=" + dataConclusao +
                '}';
    }
}
