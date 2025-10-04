package br.com.fintech.model;

import java.math.BigDecimal;

public abstract class Transacao {
    private Long id;
    private Long usuarioId;
    private String descricao;
    private Long categoriaId;
    private BigDecimal valor;

    public Transacao() {}

    public Transacao(Long id, Long usuarioId, String descricao, Long categoriaId, BigDecimal valor) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.descricao = descricao;

        //vai virar classe!
        this.categoriaId = categoriaId;

        this.valor = valor;
    }

    public Long getId() { return this.id; }

    public Long getUsuarioId() { return this.usuarioId; }

    public String getDescricao() { return this.descricao; }

    public Long getCategoriaId() { return this.categoriaId; }

    public BigDecimal getValor() { return this.valor; }

    public void setId(Long id) { this.id = id; }

    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public void setCategoria(Long categoriaId) { this.categoriaId = categoriaId; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

    public abstract String getTipoMovimentacao();

    public abstract int getMultiplicador();

    public boolean validarValor() { return this.getValor().compareTo(BigDecimal.ZERO) > 0; }

    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + id +
                ", usuario=" + usuarioId +
                ", descricao='" + descricao + '\'' +
                ", categoria='" + categoriaId + '\'' +
                ", valor=" + valor +
                '}';
    }
}