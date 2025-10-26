package br.com.fintech.service;

import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {
    @Mock
    private GastoService gastoService;
    @Mock
    private RecebimentoService recebimentoService;
    @Mock
    private InvestimentoService investimentoService;

    @InjectMocks
    private RelatorioService relatorioService;

    private final Long USER_ID = 1L;
    private final LocalDate INICIO_JAN = LocalDate.of(2025, 1, 1);
    private final LocalDate FIM_JAN = LocalDate.of(2025, 1, 31);
    private final BigDecimal SALDO_GERAL_ESPERADO = new BigDecimal("3900.00");
    private final BigDecimal SALDO_PERIODO_ESPERADO = new BigDecimal("3000.00");
    private final BigDecimal TOTAL_INVESTIDO_ESPERADO = new BigDecimal("2500.50");

    private final Gasto MOCK_ULTIMO_GASTO = new Gasto(3L, USER_ID, "Cinema", null, new BigDecimal("100.00"), LocalDate.of(2025, 3, 5));
    private final Recebimento MOCK_ULTIMO_RECEBIMENTO = new Recebimento(11L, USER_ID, "Bônus", null, new BigDecimal("1000.00"), LocalDate.of(2025, 2, 28));

    // ====================================================================
    // 1. TESTES DE CÁLCULO DE SALDO GERAL
    // ====================================================================

    @Test
    void calcularSaldoGeral_DeveCalcularSaldoCorretamente() {
        when(recebimentoService.calcularTotal(USER_ID)).thenReturn(new BigDecimal("6000.00"));
        when(gastoService.calcularTotal(USER_ID)).thenReturn(new BigDecimal("2100.00"));

        BigDecimal saldo = relatorioService.calcularSaldoGeral(USER_ID);

        assertEquals(SALDO_GERAL_ESPERADO, saldo);

        verify(recebimentoService).calcularTotal(USER_ID);
        verify(gastoService).calcularTotal(USER_ID);
    }

    @Test
    void calcularSaldoGeral_NenhumDadoDeveRetornarZero() {
        when(recebimentoService.calcularTotal(USER_ID)).thenReturn(BigDecimal.ZERO);
        when(gastoService.calcularTotal(USER_ID)).thenReturn(BigDecimal.ZERO);

        BigDecimal saldo = relatorioService.calcularSaldoGeral(USER_ID);

        assertEquals(BigDecimal.ZERO, saldo);
    }

    // ====================================================================
    // 2. TESTES DE CÁLCULO DE SALDO POR PERÍODO
    // ====================================================================

    @Test
    void calcularSaldoPeriodo_DeveFiltrarCorretamentePeloMes() {
        when(recebimentoService.calcularTotalPeriodo(USER_ID, INICIO_JAN, FIM_JAN)).thenReturn(new BigDecimal("5000.00"));
        when(gastoService.calcularTotalPeriodo(USER_ID, INICIO_JAN, FIM_JAN)).thenReturn(new BigDecimal("2000.00"));

        BigDecimal saldo = relatorioService.calcularSaldoPeriodo(USER_ID, INICIO_JAN, FIM_JAN);

        assertEquals(SALDO_PERIODO_ESPERADO, saldo);

        verify(recebimentoService).calcularTotalPeriodo(USER_ID, INICIO_JAN, FIM_JAN);
    }

    // ====================================================================
    // 3. TESTE DE CÁLCULO DE TOTAL INVESTIDO
    // ====================================================================

    @Test
    void calcularTotalInvestido_DeveSomarCorretamente() {
        when(investimentoService.calcularTotal(USER_ID)).thenReturn(TOTAL_INVESTIDO_ESPERADO);

        BigDecimal total = relatorioService.calcularTotalInvestido(USER_ID);

        assertEquals(TOTAL_INVESTIDO_ESPERADO, total);
        verify(investimentoService).calcularTotal(USER_ID);
    }

    // ====================================================================
    // 4. TESTES DE GET ULTIMO GASTO/RECEBIMENTO
    // ====================================================================

    @Test
    void getUltimoGasto_DeveRetornarOMaisRecente() {
        when(gastoService.getUltimos(USER_ID, 1)).thenReturn(List.of(MOCK_ULTIMO_GASTO));

        Gasto ultimo = relatorioService.getUltimoGasto(USER_ID).orElse(null);

        assertNotNull(ultimo);
        assertEquals("Cinema", ultimo.getDescricao());
        verify(gastoService).getUltimos(USER_ID, 1);
    }

    @Test
    void getUltimoRecebimento_DeveRetornarOMaisRecente() {
        when(recebimentoService.getUltimos(USER_ID, 1)).thenReturn(List.of(MOCK_ULTIMO_RECEBIMENTO));

        Recebimento ultimo = relatorioService.getUltimoRecebimento(USER_ID).orElse(null);

        assertNotNull(ultimo);
        assertEquals("Bônus", ultimo.getDescricao());
        verify(recebimentoService).getUltimos(USER_ID, 1);
    }

    @Test
    void getUltimoGasto_QuandoNaoHouverDeveRetornarVazio() {
        when(gastoService.getUltimos(USER_ID, 1)).thenReturn(Collections.emptyList());

        Optional<Gasto> ultimo = relatorioService.getUltimoGasto(USER_ID);

        assertTrue(ultimo.isEmpty());
    }

    @Test
    void getUltimoRecebimento_QuandoNaoHouverDeveRetornarVazio() {
        when(recebimentoService.getUltimos(USER_ID, 1)).thenReturn(Collections.emptyList());

        Optional<Recebimento> ultimo = relatorioService.getUltimoRecebimento(USER_ID);

        assertTrue(ultimo.isEmpty());
    }

    // ====================================================================
    // 5. TESTES DE GET ULTIMOS GASTOS/RECEBIMENTOS
    // ====================================================================

    @Test
    void getUltimosGastos_DeveRetornarListaLimitadaEOrdenada() {
        int limite = 2;
        List<Gasto> ultimosMock = List.of(
                new Gasto(2L, USER_ID, "Gasto B (MAIS RECENTE)", null, new BigDecimal("20"), LocalDate.of(2025, 4, 15)),
                new Gasto(3L, USER_ID, "Gasto C", null, new BigDecimal("30"), LocalDate.of(2025, 4, 5))
        );

        when(gastoService.getUltimos(USER_ID, limite)).thenReturn(ultimosMock);

        List<Gasto> ultimosGastos = relatorioService.getUltimosGastos(USER_ID, limite);

        assertEquals(limite, ultimosGastos.size());
        assertEquals("Gasto B (MAIS RECENTE)", ultimosGastos.get(0).getDescricao());
        verify(gastoService).getUltimos(USER_ID, limite);
    }

    @Test
    void getUltimosRecebimentos_DeveRetornarListaLimitadaEOrdenada() {
        int limite = 2;
        List<Recebimento> ultimosMock = List.of(
                new Recebimento(2L, USER_ID, "Recebimento B (MAIS RECENTE)", null, new BigDecimal("20"), LocalDate.of(2025, 4, 15)),
                new Recebimento(3L, USER_ID, "Recebimento C", null, new BigDecimal("30"), LocalDate.of(2025, 4, 5))
        );

        when(recebimentoService.getUltimos(USER_ID, limite)).thenReturn(ultimosMock);

        List<Recebimento> ultimosRecebimentos = relatorioService.getUltimosRecebimentos(USER_ID, limite);

        assertEquals(limite, ultimosRecebimentos.size());
        assertEquals("Recebimento B (MAIS RECENTE)", ultimosRecebimentos.get(0).getDescricao());
        verify(gastoService).getUltimos(USER_ID, limite);
    }

    // ====================================================================
    // 6. TESTE DO DTO
    // ====================================================================

    @Test
    void getDashboard_DeveMontarODTOComTodosOsValoresCorretos() {
        int limite = 1;

        when(recebimentoService.calcularTotal(USER_ID)).thenReturn(new BigDecimal("6000.00"));
        when(gastoService.calcularTotal(USER_ID)).thenReturn(new BigDecimal("2100.00"));
        when(investimentoService.calcularTotal(USER_ID)).thenReturn(TOTAL_INVESTIDO_ESPERADO);

        when(recebimentoService.calcularTotalPeriodo(USER_ID, INICIO_JAN, FIM_JAN)).thenReturn(new BigDecimal("5000.00"));
        when(gastoService.calcularTotalPeriodo(USER_ID, INICIO_JAN, FIM_JAN)).thenReturn(new BigDecimal("2000.00"));

        when(gastoService.getUltimos(USER_ID, limite)).thenReturn(List.of(MOCK_ULTIMO_GASTO));
        when(recebimentoService.getUltimos(USER_ID, limite)).thenReturn(List.of(MOCK_ULTIMO_RECEBIMENTO));

        var dashboard = relatorioService.getDashboard(USER_ID, limite, INICIO_JAN, FIM_JAN);

        assertEquals(SALDO_GERAL_ESPERADO, dashboard.getSaldoGeral());
        assertEquals(SALDO_PERIODO_ESPERADO, dashboard.getSaldoPeriodo());
        assertEquals(TOTAL_INVESTIDO_ESPERADO, dashboard.getTotalInvestido());

        assertEquals(MOCK_ULTIMO_GASTO.getDescricao(), dashboard.getUltimoGasto().getDescricao());
        assertEquals(MOCK_ULTIMO_RECEBIMENTO.getDescricao(), dashboard.getUltimoRecebimento().getDescricao());

        assertEquals(limite, dashboard.getUltimosGastos().size());
        assertEquals(limite, dashboard.getUltimosRecebimentos().size());
    }
}