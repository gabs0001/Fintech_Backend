package br.com.fintech.service;

import br.com.fintech.dao.GastoDAO;
import br.com.fintech.dao.InvestimentoDAO;
import br.com.fintech.dao.RecebimentoDAO;
import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Investimento;
import br.com.fintech.model.Recebimento;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Recebimento> todosOsRecebimentos = this.recebimentoDAO.getAll();
        List<Gasto> todosOsGastos = this.gastoDAO.getAll();

        BigDecimal totalRecebimentos = todosOsRecebimentos.stream()
                .filter(r -> userId.equals(r.getUsuarioId()))
                .map(Recebimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGastos = todosOsGastos.stream()
                .filter(g -> userId.equals(g.getUsuarioId()))
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalRecebimentos.subtract(totalGastos);
    }

    public BigDecimal calcularSaldoPeriodo(Long userId, LocalDate inicio, LocalDate fim) throws SQLException {
        List<Recebimento> todosOsRecebimentos = this.recebimentoDAO.getAll();
        List<Gasto> todosOsGastos = this.gastoDAO.getAll();

        BigDecimal totalRecebimentosPeriodo = todosOsRecebimentos.stream()
                .filter(r -> userId.equals(r.getUsuarioId()))
                .filter(r -> {
                    LocalDate data = r.getDataRecebimento();
                    return (data.isAfter(inicio) || data.isEqual(inicio)) &&
                            (data.isBefore(fim) || data.isEqual(fim));
                })
                .map(Recebimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGastosPeriodo = todosOsGastos.stream()
                .filter(g -> userId.equals(g.getUsuarioId()))
                .filter(g -> {
                    LocalDate data = g.getDataGasto();
                    return (data.isAfter(inicio) || data.isEqual(inicio)) &&
                            (data.isBefore(fim) || data.isEqual(fim));
                })
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalRecebimentosPeriodo.subtract(totalGastosPeriodo);
    }

    public BigDecimal calcularTotalInvestido(Long userId) throws SQLException {
        List<Investimento> todosOsInvestimentos = this.investimentoDAO.getAll();

        BigDecimal totalInvestimentos = todosOsInvestimentos.stream()
                .filter(i -> userId.equals(i.getUsuarioId()))
                .map(Investimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalInvestimentos;
    }

    public Gasto getUltimoGasto(Long userId) throws SQLException {
        List<Gasto> todosOsGastos = this.gastoDAO.getAll();

        Optional<Gasto> ultimoGasto = todosOsGastos.stream()
                .filter(g -> userId.equals(g.getUsuarioId()))
                .max(Comparator.comparing(Gasto::getDataGasto));

        return ultimoGasto.orElse(null);
    }

    public List<Gasto> getUltimosGastos(Long userId, int limite) throws SQLException {
        List<Gasto> todosOsGastos = this.gastoDAO.getAll();

        return todosOsGastos.stream()
                .filter(g -> userId.equals(g.getUsuarioId()))
                .sorted(Comparator.comparing(Gasto::getDataGasto).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    public Recebimento getUltimoRecebimento(Long userId) throws SQLException {
        List<Recebimento> todosOsRecebimentos = this.recebimentoDAO.getAll();

        Optional<Recebimento> ultimoRecebimento = todosOsRecebimentos.stream()
                .filter(g -> userId.equals(g.getUsuarioId()))
                .max(Comparator.comparing(Recebimento::getDataRecebimento));

        return ultimoRecebimento.orElse(null);
    }

    public List<Recebimento> getUltimosRecebimentos(Long userId, int limite) throws SQLException {
        List<Recebimento> todosOsRecebimentos = recebimentoDAO.getAll();

        return todosOsRecebimentos.stream()
                .filter(g -> userId.equals(g.getUsuarioId()))
                .sorted(Comparator.comparing(Recebimento::getDataRecebimento).reversed())
                .limit(limite)
                .collect(Collectors.toList());
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