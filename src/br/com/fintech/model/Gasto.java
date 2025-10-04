package br.com.fintech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Gasto extends Transacao {
    private LocalDate dataGasto;

    public Gasto() {}

    public Gasto(Long id, Long usuarioId, String descricao, Long categoriaId, BigDecimal valor, LocalDate dataGasto) {
        super(id, usuarioId, descricao, categoriaId, valor);
        this.dataGasto = dataGasto;
    }

    public LocalDate getDataGasto() { return dataGasto; }

    public void setDataGasto(LocalDate dataGasto) { this.dataGasto = dataGasto; }

    @Override

    public String getTipoMovimentacao() { return "Gasto"; }

    public int getMultiplicador() { return -1; }

    public String toString() {
        return "Gasto{" +
                super.toString().substring(super.toString().indexOf("{") + 1, super.toString().length() - 1) +
                ", dataGasto=" + this.dataGasto +
                '}';
    }
}