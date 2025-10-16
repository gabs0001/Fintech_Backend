package br.com.fintech.service;

import br.com.fintech.dao.GastoDAO;
import br.com.fintech.dao.InvestimentoDAO;
import br.com.fintech.dao.RecebimentoDAO;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import br.com.fintech.model.Investimento;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {
    @Mock
    private GastoDAO gastoDAO;
    @Mock
    private RecebimentoDAO recebimentoDAO;
    @Mock
    private InvestimentoDAO investimentoDAO;

    @InjectMocks
    private RelatorioService relatorioService;

    private final Long USER_ID = 1L;
    private final Long OTHER_USER_ID = 2L;

    // --- Mocks de Dados Fictícios ---
    private final List<Recebimento> MOCK_RECEBIMENTOS = List.of(
            new Recebimento(10L, USER_ID, "Salário", null, new BigDecimal("5000.00"),
                    LocalDate.of(2025, 1, 15)),
            new Recebimento(11L, USER_ID, "Bônus", null, new BigDecimal("1000.00"),
                    LocalDate.of(2025, 2, 28)),
            new Recebimento(12L, OTHER_USER_ID, "Outro User", null, new BigDecimal("100.00"),
                    LocalDate.of(2025, 1, 10))
    );

    private final List<Gasto> MOCK_GASTOS = List.of(
            new Gasto(1L, USER_ID, "Aluguel", null, new BigDecimal("1500.00"),
                    LocalDate.of(2025, 1, 1)),
            new Gasto(2L, USER_ID, "Mercado", null, new BigDecimal("500.00"),
                    LocalDate.of(2025, 1, 10)),
            new Gasto(3L, USER_ID, "Cinema", null, new BigDecimal("100.00"),
                    LocalDate.of(2025, 3, 5)), // Mais recente
            new Gasto(4L, OTHER_USER_ID, "Gasto Outro User", null, new BigDecimal("50.00"), LocalDate.now())
    );

    private final List<Investimento> MOCK_INVESTIMENTOS = List.of(
            new Investimento(1L, USER_ID, "CDB", null, new BigDecimal("2000.00"), "CDB",
                    LocalDate.now(), null, null),
            new Investimento(2L, USER_ID, "Ações", null, new BigDecimal("500.50"), "Ação",
                    LocalDate.now(), null, null),
            new Investimento(3L, OTHER_USER_ID, "Poupanca", null, new BigDecimal("100.00"), "P",
                    LocalDate.now(), null, null)
    );

    // ====================================================================
    // 1. TESTES DE CÁLCULO DE SALDO GERAL
    // ====================================================================

    @Test
    void calcularSaldoGeral_DeveCalcularSaldoCorretamente() throws SQLException {
        when(recebimentoDAO.getAll()).thenReturn(MOCK_RECEBIMENTOS);
        when(gastoDAO.getAll()).thenReturn(MOCK_GASTOS);

        BigDecimal saldo = relatorioService.calcularSaldoGeral(USER_ID);

        assertEquals(new BigDecimal("3900.00"), saldo);
    }

    @Test
    void calcularSaldoGeral_NenhumDadoDeveRetornarZero() throws SQLException {
        when(recebimentoDAO.getAll()).thenReturn(Collections.emptyList());
        when(gastoDAO.getAll()).thenReturn(Collections.emptyList());

        BigDecimal saldo = relatorioService.calcularSaldoGeral(USER_ID);

        assertEquals(BigDecimal.ZERO, saldo);
    }

    // ====================================================================
    // 2. TESTES DE CÁLCULO DE SALDO POR PERÍODO
    // ====================================================================

    @Test
    void calcularSaldoPeriodo_DeveFiltrarCorretamentePeloMes() throws SQLException {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);

        when(recebimentoDAO.getAll()).thenReturn(MOCK_RECEBIMENTOS);
        when(gastoDAO.getAll()).thenReturn(MOCK_GASTOS);

        BigDecimal saldo = relatorioService.calcularSaldoPeriodo(USER_ID, inicio, fim);

        assertEquals(new BigDecimal("3000.00"), saldo);
    }

    // ====================================================================
    // 3. TESTE DE CÁLCULO DE TOTAL INVESTIDO
    // ====================================================================

    @Test
    void calcularTotalInvestido_DeveSomarCorretamente() throws SQLException {
        when(investimentoDAO.getAll()).thenReturn(MOCK_INVESTIMENTOS);

        BigDecimal total = relatorioService.calcularTotalInvestido(USER_ID);

        assertEquals(new BigDecimal("2500.50"), total);
    }

    // ====================================================================
    // 4. TESTES DE GET ULTIMO GASTO/RECEBIMENTO
    // ====================================================================

    @Test
    void getUltimoGasto_DeveRetornarOMaisRecente() throws SQLException {
        when(gastoDAO.getAll()).thenReturn(MOCK_GASTOS);

        Gasto ultimo = relatorioService.getUltimoGasto(USER_ID);

        assertNotNull(ultimo);
        assertEquals("Cinema", ultimo.getDescricao());
    }

    @Test
    void getUltimoRecebimento_DeveRetornarOMaisRecente() throws SQLException {
        when(recebimentoDAO.getAll()).thenReturn(MOCK_RECEBIMENTOS);

        Recebimento ultimo = relatorioService.getUltimoRecebimento(USER_ID);

        assertNotNull(ultimo);
        assertEquals("Bônus", ultimo.getDescricao());
    }

    // ====================================================================
    // 5. TESTES DE GET ULTIMOS GASTOS/RECEBIMENTOS (Lista e Ordenação)
    // ====================================================================

    @Test
    void getUltimosGastos_DeveRetornarListaLimitadaEOrdenada() throws SQLException {
        int limite = 2;

        List<Gasto> gastosParaTeste = List.of(
                new Gasto(1L, USER_ID, "Gasto A", null, new BigDecimal("10"),
                        LocalDate.of(2025, 4, 1)),
                new Gasto(2L, USER_ID, "Gasto B (MAIS RECENTE)", null, new BigDecimal("20"),
                        LocalDate.of(2025, 4, 15)),
                new Gasto(3L, USER_ID, "Gasto C", null, new BigDecimal("30"),
                        LocalDate.of(2025, 4, 5)),
                new Gasto(4L, USER_ID, "Gasto D (MAIS ANTIGO)", null, new BigDecimal("40"),
                        LocalDate.of(2025, 3, 1))
        );

        when(gastoDAO.getAll()).thenReturn(gastosParaTeste);

        List<Gasto> ultimosGastos = relatorioService.getUltimosGastos(USER_ID, limite);

        assertEquals(limite, ultimosGastos.size(), "A lista deve ter exatamente o tamanho do limite.");

        assertEquals("Gasto B (MAIS RECENTE)", ultimosGastos.get(0).getDescricao());
        assertEquals("Gasto C", ultimosGastos.get(1).getDescricao());
    }

    @Test
    void getUltimosRecebimentos_DeveRetornarListaLimitadaEOrdenada() throws SQLException {
        int limite = 2;

        List<Recebimento> recebimentosParaTeste = List.of(
                new Recebimento(1L, USER_ID, "Recebimento A", null, new BigDecimal("10"),
                        LocalDate.of(2025, 4, 1)),
                new Recebimento(2L, USER_ID, "Recebimento B (MAIS RECENTE)", null, new BigDecimal("20"),
                        LocalDate.of(2025, 4, 15)),
                new Recebimento(3L, USER_ID, "Recebimento C", null, new BigDecimal("30"),
                        LocalDate.of(2025, 4, 5)),
                new Recebimento(4L, USER_ID, "Recebimento D (MAIS ANTIGO)", null, new BigDecimal("40"),
                        LocalDate.of(2025, 3, 1))
        );

        when(recebimentoDAO.getAll()).thenReturn(recebimentosParaTeste);

        List<Recebimento> ultimosRecebimentos = relatorioService.getUltimosRecebimentos(USER_ID, limite);

        assertEquals(limite, ultimosRecebimentos.size(), "A lista deve ter exatamente o tamanho do limite.");

        assertEquals("Recebimento B (MAIS RECENTE)", ultimosRecebimentos.get(0).getDescricao());
        assertEquals("Recebimento C", ultimosRecebimentos.get(1).getDescricao());
    }

    // ====================================================================
    // 6. TESTE DO DTO
    // ====================================================================

    @Test
    void getDashboard_DeveMontarODTOComTodosOsValoresCorretos() throws SQLException {
        when(recebimentoDAO.getAll()).thenReturn(MOCK_RECEBIMENTOS);
        when(gastoDAO.getAll()).thenReturn(MOCK_GASTOS);
        when(investimentoDAO.getAll()).thenReturn(MOCK_INVESTIMENTOS);

        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);
        int limite = 1;

        var dashboard = relatorioService.getDashboard(USER_ID, limite, inicio, fim);

        assertEquals(new BigDecimal("3900.00"), dashboard.getSaldoGeral());
        assertEquals(new BigDecimal("3000.00"), dashboard.getSaldoPeriodo());
        assertEquals(new BigDecimal("2500.50"), dashboard.getTotalInvestido());

        assertEquals("Cinema", dashboard.getUltimoGasto().getDescricao());
        assertEquals("Bônus", dashboard.getUltimoRecebimento().getDescricao());

        assertEquals(limite, dashboard.getUltimosGastos().size());
        assertEquals(limite, dashboard.getUltimosRecebimentos().size());
    }
}