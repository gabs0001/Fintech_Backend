package br.com.fintech.service;

import br.com.fintech.dao.GastoDAO;
import br.com.fintech.dao.InvestimentoDAO;
import br.com.fintech.dao.RecebimentoDAO;
import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioService {
    private final GastoDAO gastoDAO;
    private final RecebimentoDAO recebimentoDAO;
    private final InvestimentoDAO investimentoDAO;

    public RelatorioService(GastoDAO gastoDAO, RecebimentoDAO recebimentoDAO, InvestimentoDAO investimentoDAO) {
        this.gastoDAO = gastoDAO;
        this.recebimentoDAO = recebimentoDAO;
        this.investimentoDAO = investimentoDAO;
    }

    public BigDecimal calcularSaldoGeral(Long userId) throws SQLException {
        BigDecimal totalRecebimentos = this.recebimentoDAO.calcularTotal(userId);
        BigDecimal totalGastos = this.gastoDAO.calcularTotal(userId);

        return totalRecebimentos.subtract(totalGastos);
    }

    public BigDecimal calcularSaldoPeriodo(Long userId, LocalDate inicio, LocalDate fim) throws SQLException {
        BigDecimal totalRecebimentosPeriodo = this.recebimentoDAO.calcularTotalPeriodo(userId, inicio, fim);
        BigDecimal totalGastosPeriodo = this.gastoDAO.calcularTotalPeriodo(userId, inicio, fim);

        return totalRecebimentosPeriodo.subtract(totalGastosPeriodo);
    }

    public BigDecimal calcularTotalInvestido(Long userId) throws SQLException {
        return this.investimentoDAO.calcularTotal(userId);
    }

    public Gasto getUltimoGasto(Long userId) throws SQLException {
        return this.gastoDAO.getUltimo(userId);
    }

    public List<Gasto> getUltimosGastos(Long userId, int limite) throws SQLException {
        return this.gastoDAO.getUltimos(userId, limite);
    }

    public Recebimento getUltimoRecebimento(Long userId) throws SQLException {
        return this.recebimentoDAO.getUltimo(userId);
    }

    public List<Recebimento> getUltimosRecebimentos(Long userId, int limite) throws SQLException {
        return this.recebimentoDAO.getUltimos(userId, limite);
    }

    public DashboardDTO getDashboard(Long userId, int limite, LocalDate inicio, LocalDate fim) throws SQLException {
        BigDecimal saldoGeral = this.calcularSaldoGeral(userId);
        BigDecimal saldoPeriodo = this.calcularSaldoPeriodo(userId, inicio, fim);
        BigDecimal totalInvestido = this.calcularTotalInvestido(userId);
        Gasto ultimoGasto = this.getUltimoGasto(userId);
        List<Gasto> ultimosGastos = this.getUltimosGastos(userId, limite);
        Recebimento ultimoRecebimento = this.getUltimoRecebimento(userId);
        List<Recebimento> ultimosRecebimentos = this.getUltimosRecebimentos(userId, limite);

        return new DashboardDTO(
                saldoGeral,
                saldoPeriodo,
                totalInvestido,
                ultimoGasto,
                ultimosGastos,
                ultimoRecebimento,
                ultimosRecebimentos,
                userId
        );
    }
}