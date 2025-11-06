package br.com.fintech.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@JsonIgnoreProperties({" hibernateLazyInitializer", "handler" })
@Table(name = "T_SIF_GASTO")
@AttributeOverrides({
  @AttributeOverride(name = "id", column = @Column(name = "COD_GASTO")),
  @AttributeOverride(name = "usuarioId", column = @Column(name = "COD_USUARIO")),
  @AttributeOverride(name = "descricao", column = @Column(name = "DES_GASTO")),
  @AttributeOverride(name = "valor", column = @Column(name = "VAL_GASTO"))
})
public class Gasto extends Transacao {
    @Column(name = "DAT_GASTO")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataGasto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_CATEGORIA_GASTO", nullable = false)
    private CategoriaGasto categoriaGasto;

    public Gasto() {
        super();
    }

    public Gasto(Long usuarioId, String descricao, CategoriaGasto categoriaGasto, BigDecimal valor, LocalDate dataGasto) {
        super(usuarioId, descricao, valor);
        this.dataGasto = dataGasto;
        this.categoriaGasto = categoriaGasto;
    }

    public LocalDate getDataGasto() { return dataGasto; }

    public void setDataGasto(LocalDate dataGasto) { this.dataGasto = dataGasto; }

    public CategoriaGasto getCategoriaGasto() {
        return this.categoriaGasto;
    }

    public Long getCategoriaGastoId() {
        return this.categoriaGasto != null ? this.categoriaGasto.getId() : null;
    }

    public void setCategoriaGasto(CategoriaGasto categoriaGasto) {
        this.categoriaGasto = categoriaGasto;
    }

    @Override
    @Transient
    public String getTipoMovimentacao() { return "Gasto"; }

    @Override
    @Transient
    public int getMultiplicador() { return -1; }

    @Override
    public String toString() {
        return "Gasto{" +
                super.toString().substring(super.toString().indexOf("{") + 1, super.toString().length() - 1) +
                ", dataGasto=" + dataGasto +
                ", categoriaGasto=" + (categoriaGasto != null ? categoriaGasto.getDescricao() : "N/A") +
                '}';
    }
}