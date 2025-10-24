package br.com.fintech.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@MappedSuperclass
public abstract class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COD_TRANSACAO")
    private Long id;

    @Column(name = "COD_USUARIO", nullable = false)
    private Long usuarioId;

    @Column(name = "DES_TRANSACAO", nullable = false)
    private String descricao;

    @Column(name = "VAL_TRANSACAO", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    public Transacao() {}

    public Transacao(Long id, Long usuarioId, String descricao, BigDecimal valor) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.descricao = descricao;
        this.valor = valor;
    }

    public Long getId() { return this.id; }

    public Long getUsuarioId() { return this.usuarioId; }

    public String getDescricao() { return this.descricao; }

    public BigDecimal getValor() { return this.valor; }

    public void setId(Long id) { this.id = id; }

    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

    public abstract String getTipoMovimentacao();

    public abstract int getMultiplicador();

    public boolean validarValor() { return this.getValor().compareTo(BigDecimal.ZERO) > 0; }

    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                '}';
    }
}