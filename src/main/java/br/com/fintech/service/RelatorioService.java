package br.com.fintech.service;

import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.dto.GastoDTO;
import br.com.fintech.dto.RecebimentoDTO;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RelatorioService {
    private final GastoService gastoService;
    private final RecebimentoService recebimentoService;
    private final InvestimentoService investimentoService;

    public RelatorioService(
            GastoService gastoService,
            RecebimentoService recebimentoService,
            InvestimentoService investimentoService
    ) {
        this.gastoService = gastoService;
        this.recebimentoService = recebimentoService;
        this.investimentoService = investimentoService;
    }

    public BigDecimal calcularSaldoGeral(Long userId) {
        BigDecimal totalRecebimentos = this.recebimentoService.calcularTotal(userId);
        BigDecimal totalGastos = this.gastoService.calcularTotal(userId);

        return totalRecebimentos.subtract(totalGastos);
    }

    public BigDecimal calcularSaldoPeriodo(Long userId, LocalDate inicio, LocalDate fim) {
        BigDecimal totalRecebimentosPeriodo = this.recebimentoService.calcularTotalPeriodo(userId, inicio, fim);
        BigDecimal totalGastosPeriodo = this.gastoService.calcularTotalPeriodo(userId, inicio, fim);

        return totalRecebimentosPeriodo.subtract(totalGastosPeriodo);
    }

    public BigDecimal calcularTotalInvestido(Long userId) {
        return this.investimentoService.calcularTotal(userId);
    }

    public Optional<Gasto> getUltimoGasto(Long userId) {
        List<Gasto> ultimos = this.gastoService.getUltimos(userId, 1);
        return ultimos.isEmpty() ? Optional.empty() : Optional.of(ultimos.get(0));
    }

    public List<Gasto> getUltimosGastos(Long userId, int limite) {
        return this.gastoService.getUltimos(userId, limite);
    }

    public Optional<Recebimento> getUltimoRecebimento(Long userId) {
        List<Recebimento> ultimos = this.recebimentoService.getUltimos(userId, 1);
        return ultimos.isEmpty() ? Optional.empty() : Optional.of(ultimos.get(0));
    }

    public List<Recebimento> getUltimosRecebimentos(Long userId, int limite) {
        return this.recebimentoService.getUltimos(userId, limite);
    }

    public DashboardDTO getDashboard(Long userId, int limite, LocalDate inicio, LocalDate fim) {
        BigDecimal saldoGeral = this.calcularSaldoGeral(userId);
        BigDecimal saldoPeriodo = this.calcularSaldoPeriodo(userId, inicio, fim);
        BigDecimal totalInvestido = this.calcularTotalInvestido(userId);

        Optional<Gasto> ultimoGastoOpt = this.getUltimoGasto(userId);
        List<Gasto> ultimosGastos = this.gastoService.getUltimosPorPeriodo(userId, limite, inicio, fim);
        Optional<Recebimento> ultimoRecebimentoOpt = this.getUltimoRecebimento(userId);
        List<Recebimento> ultimosRecebimentos = this.recebimentoService.getUltimosPorPeriodo(userId, limite, inicio, fim);

        GastoDTO ultimoGastoDTO = ultimoGastoOpt.map(GastoDTO::new).orElse(null);
        List<GastoDTO> ultimosGastosDTO = ultimosGastos.stream()
                .map(GastoDTO::new)
                .toList();

        RecebimentoDTO ultimoRecebimentoDTO = ultimoRecebimentoOpt.map(RecebimentoDTO::new).orElse(null);
        List<RecebimentoDTO> ultimosRecebimentosDTO = ultimosRecebimentos.stream()
                .map(RecebimentoDTO::new)
                .toList();

        return new DashboardDTO(
                saldoGeral,
                saldoPeriodo,
                totalInvestido,
                ultimoGastoDTO,
                ultimosGastosDTO,
                ultimoRecebimentoDTO,
                ultimosRecebimentosDTO,
                userId
        );
    }
}