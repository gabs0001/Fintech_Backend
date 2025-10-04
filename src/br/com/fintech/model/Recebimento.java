package br.com.fintech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Recebimento extends Transacao {
    private LocalDate dataRecebimento;

    public Recebimento() {}

    public Recebimento(Long id, Long usuarioId, String descricao, Long categoriaId, BigDecimal valor, LocalDate dataRecebimento) {
        super(id, usuarioId, descricao, categoriaId, valor);
        this.dataRecebimento = dataRecebimento;
    }

    public LocalDate getDataRecebimento() { return this.dataRecebimento; }

    public void setDataRecebimento(LocalDate dataRecebimento) { this.dataRecebimento = dataRecebimento; }

    @Override

    public String getTipoMovimentacao() { return "Recebimento"; }

    public int getMultiplicador() { return 1; }

    public String toString() {
        return "Recebimento{" +
                super.toString().substring(super.toString().indexOf("{") + 1, super.toString().length() - 1) +
                ", dataRecebimento=" + this.dataRecebimento +
                '}';
    }
}