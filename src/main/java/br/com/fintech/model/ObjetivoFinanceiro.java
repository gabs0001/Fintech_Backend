package br.com.fintech.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "T_SIF_OBJETIVO_FINANCEIRO")
public class ObjetivoFinanceiro implements OwnedEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_SIF_OBJETIVO_FINANCEIRO"
    )
    @SequenceGenerator(
            name = "SEQ_SIF_OBJETIVO_FINANCEIRO",
            sequenceName = "SEQ_SIF_OBJETIVO_FINANCEIRO",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "COD_USUARIO", nullable = false)
    private Long usuarioId;

    @Column(name = "NOM_OBJETIVO", length = 100, nullable = false)
    private String nome;

    @Column(name = "DES_OBJETIVO", length = 100, nullable = false)
    private String descricao;

    @Column(name = "VAL_OBJETIVO", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "DAT_CONCLUSAO_OBJETIVO", nullable = false)
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

    @Override
    public Long getUsuarioId() { return this.usuarioId; }

    public String getNome() { return this.nome; }

    public String getDescricao() { return this.descricao; }

    public BigDecimal getValor() { return this.valor; }

    public LocalDate getDataConclusao() { return this.dataConclusao; }

    public void setId(Long id) { this.id = id; }

    @Override
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

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
