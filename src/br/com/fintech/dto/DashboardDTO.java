package br.com.fintech.dto;

import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {
    private BigDecimal saldoGeral;
    private BigDecimal saldoPeriodo;
    private BigDecimal totalInvestido;
    private Gasto ultimoGasto;
    private List<Gasto> ultimosGastos;
    private Recebimento ultimoRecebimento;
    private List<Recebimento> ultimosRecebimentos;
    private Long userId;

    public DashboardDTO(
            BigDecimal saldoGeral,
            BigDecimal saldoPeriodo,
            BigDecimal totalInvestido,
            Gasto ultimoGasto,
            List<Gasto> ultimosGastos,
            Recebimento ultimoRecebimento,
            List<Recebimento> ultimosRecebimentos,
            Long userId
    ) {
        this.saldoGeral = saldoGeral;
        this.saldoPeriodo = saldoPeriodo;
        this.totalInvestido = totalInvestido;
        this.ultimoGasto = ultimoGasto;
        this.ultimosGastos = ultimosGastos;
        this.ultimoRecebimento = ultimoRecebimento;
        this.ultimosRecebimentos = ultimosRecebimentos;
        this.userId = userId;
    }

    public BigDecimal getSaldoGeral() { return this.saldoGeral; }

    public BigDecimal getSaldoPeriodo() { return this.saldoPeriodo; }

    public BigDecimal getTotalInvestido() { return this.totalInvestido; }

    public Gasto getUltimoGasto() { return this.ultimoGasto; }

    public List<Gasto> getUltimosGastos() { return this.ultimosGastos; }

    public Recebimento getUltimoRecebimento() { return this.ultimoRecebimento; }

    public List<Recebimento> getUltimosRecebimentos() { return this.ultimosRecebimentos; }

    public Long getUserId() { return this.userId; }
}
