package br.com.fintech.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "T_SIF_INVESTIMENTO")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "COD_INVESTIMENTO")),
        @AttributeOverride(name = "usuarioId", column = @Column(name = "COD_USUARIO")),
        @AttributeOverride(name = "descricao", column = @Column(name = "DES_INVESTIMENTO")),
        @AttributeOverride(name = "valor", column = @Column(name = "VAL_INVESTIMENTO"))
})
public class Investimento extends Transacao {
    @Column(name = "NOM_APLICACAO", length = 100, nullable = false)
    private String nome;

    @Column(name = "DAT_REALIZACAO", nullable = false)
    private LocalDate dataRealizacao;

    @Column(name = "DAT_VENCIMENTO")
    private LocalDate dataVencimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_INSTITUICAO", nullable = false)
    private Instituicao instituicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_TIPO_INVESTIMENTO", nullable = false)
    private TipoInvestimento tipoInvestimento;

    public Investimento() {}

    public Investimento(Long usuarioId, String descricao, TipoInvestimento tipoInvestimento, BigDecimal valor, String nome, LocalDate dataRealizacao, LocalDate dataVencimento, Instituicao instituicao) {
        super(usuarioId, descricao, valor);
        this.nome = nome;
        this.dataRealizacao = dataRealizacao;
        this.dataVencimento = dataVencimento;
        this.instituicao = instituicao;
        this.tipoInvestimento = tipoInvestimento;
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

    public TipoInvestimento getTipoInvestimento() { return tipoInvestimento; }

    public Long getTipoInvestimentoId() { return this.tipoInvestimento != null ? this.tipoInvestimento.getId() : null; }

    public void setTipoInvestimento(TipoInvestimento tipoInvestimento) { this.tipoInvestimento = tipoInvestimento; }

    @Override
    @Transient
    public String getTipoMovimentacao() {
        return "Investimento";
    }

    @Override
    @Transient
    public int getMultiplicador() {
        return -1;
    }

    @Override
    public String toString() {
        return "Investimento{" +
                super.toString().substring(super.toString().indexOf("{") + 1, super.toString().length() - 1) + // Inclui a Transacao
                ", nome='" + nome + '\'' +
                ", dataRealizacao=" + dataRealizacao +
                ", dataVencimento=" + dataVencimento +
                ", instituicao='" + (instituicao != null ? instituicao.getNome() : "N/A") + '\'' + // Exibe nome da Inst.
                ", tipoInvestimento='" + (tipoInvestimento != null ? tipoInvestimento.getDescricao() : "N/A") + '\'' + // Exibe descrição do Tipo
                '}';
    }
}