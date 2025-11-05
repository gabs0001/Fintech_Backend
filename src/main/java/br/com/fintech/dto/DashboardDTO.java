package br.com.fintech.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {
    private BigDecimal saldoGeral;
    private BigDecimal saldoPeriodo;
    private BigDecimal totalInvestido;
    private GastoDTO ultimoGasto;
    private List<GastoDTO> ultimosGastos;
    private RecebimentoDTO ultimoRecebimento;
    private List<RecebimentoDTO> ultimosRecebimentos;
    private Long userId;

    public DashboardDTO(
            BigDecimal saldoGeral,
            BigDecimal saldoPeriodo,
            BigDecimal totalInvestido,
            GastoDTO ultimoGasto,
            List<GastoDTO> ultimosGastos,
            RecebimentoDTO ultimoRecebimento,
            List<RecebimentoDTO> ultimosRecebimentos,
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

    public BigDecimal getSaldoGeral() {
        return saldoGeral;
    }

    public BigDecimal getSaldoPeriodo() {
        return saldoPeriodo;
    }

    public BigDecimal getTotalInvestido() {
        return totalInvestido;
    }

    public GastoDTO getUltimoGasto() {
        return ultimoGasto;
    }

    public List<GastoDTO> getUltimosGastos() {
        return ultimosGastos;
    }

    public RecebimentoDTO getUltimoRecebimento() {
        return ultimoRecebimento;
    }

    public List<RecebimentoDTO> getUltimosRecebimentos() {
        return ultimosRecebimentos;
    }

    public Long getUserId() {
        return userId;
    }
}