package br.com.fintech.controller;

import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.service.GastoService;
import br.com.fintech.service.RecebimentoService;
import br.com.fintech.service.InvestimentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GastoService gastoService;

    @Mock
    private RecebimentoService recebimentoService;

    @Mock
    private InvestimentoService investimentoService;

    @InjectMocks
    private DashboardController dashboardController;

    private static final Long MOCK_USER_ID = 1L;
    private static final int ANO_TESTE = 2025;
    private static final int MES_TESTE = 9;
    private static final LocalDate INICIO_PERIODO = LocalDate.of(ANO_TESTE, MES_TESTE, 1);
    private static final LocalDate FIM_PERIODO = LocalDate.of(ANO_TESTE, MES_TESTE, 30);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ----------------------------------------------------
    // TESTE DE ENDPOINT: Resumo Mensal (Calcula Saldo)
    // ----------------------------------------------------

    @Test
    @DisplayName("GET /api/dashboard/saldo-geral - Deve retornar 200 OK com os saldos calculados")
    void getResumoMensal_DeveRetornar200OKComDadosCorretos() throws Exception {
        BigDecimal totalRecebimentos = new BigDecimal("8000.00");
        BigDecimal totalGastos = new BigDecimal("3500.00");
        BigDecimal totalInvestimento = new BigDecimal("50000.00");
        BigDecimal saldoMensal = totalRecebimentos.subtract(totalGastos);

        when(recebimentoService.calcularTotalPeriodo(
                eq(MOCK_USER_ID),
                eq(INICIO_PERIODO),
                eq(FIM_PERIODO)))
                .thenReturn(totalRecebimentos);

        when(gastoService.calcularTotalPeriodo(
                eq(MOCK_USER_ID),
                eq(INICIO_PERIODO),
                eq(FIM_PERIODO)))
                .thenReturn(totalGastos);

        when(investimentoService.calcularTotal(MOCK_USER_ID)).thenReturn(totalInvestimento);

        mockMvc.perform(get("/api/dashboard/saldo-geral")
                        .param("ano", String.valueOf(ANO_TESTE))
                        .param("mes", String.valueOf(MES_TESTE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoPeriodo").value(saldoMensal))
                .andExpect(jsonPath("$.totalRecebidoPeriodo").value(totalRecebimentos))
                .andExpect(jsonPath("$.totalGastoPeriodo").value(totalGastos))
                .andExpect(jsonPath("$.totalInvestido").value(totalInvestimento));

        verify(recebimentoService).calcularTotalPeriodo(MOCK_USER_ID, INICIO_PERIODO, FIM_PERIODO);
        verify(gastoService).calcularTotalPeriodo(MOCK_USER_ID, INICIO_PERIODO, FIM_PERIODO);
        verify(investimentoService).calcularTotal(MOCK_USER_ID);
    }

    // ----------------------------------------------------
    // TESTE DE ENDPOINT: Saldo Zero
    // ----------------------------------------------------

    @Test
    @DisplayName("GET /api/dashboard/saldo-geral - Deve retornar saldo zero quando não houver transações")
    void getResumoMensal_QuandoSemDados_DeveRetornarZero() throws Exception {
        when(recebimentoService.calcularTotalPeriodo(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(gastoService.calcularTotalPeriodo(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(investimentoService.calcularTotal(any())).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/api/dashboard/saldo-geral")
                        .param("ano", String.valueOf(ANO_TESTE))
                        .param("mes", String.valueOf(MES_TESTE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoPeriodo").value(0.00))
                .andExpect(jsonPath("$.totalRecebidoPeriodo").value(0.00))
                .andExpect(jsonPath("$.totalGastoPeriodo").value(0.00))
                .andExpect(jsonPath("$.totalInvestido").value(0.00));
    }
}