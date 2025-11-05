package br.com.fintech.controller;

import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.dto.GastoDTO;
import br.com.fintech.dto.RecebimentoDTO;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.GastoService;
import br.com.fintech.service.RecebimentoService;
import br.com.fintech.service.InvestimentoService;
import br.com.fintech.service.RelatorioService;
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
import java.util.Collections;

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

    @Mock
    private RelatorioService relatorioService;

    @InjectMocks
    private DashboardController dashboardController;

    private static final Long MOCK_USER_ID = 1L;
    private static final int ANO_TESTE = 2025;
    private static final int MES_TESTE = 9;
    private static final int LIMITE_PADRAO = 5;
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

    @Test
    @DisplayName("GET /api/dashboard - Deve retornar 200 OK com o DashboardDTO completo")
    void buscarTodos_DeveRetornar200OKComDadosCorretos() throws Exception {
        BigDecimal saldoGeral = new BigDecimal("70000.0");
        BigDecimal saldoPeriodo = new BigDecimal("4500.0");
        BigDecimal totalInvestido = new BigDecimal("50000.0");

        GastoDTO gastoDTO = new GastoDTO(new Gasto());
        RecebimentoDTO recebimentoDTO = new RecebimentoDTO(new Recebimento());

        DashboardDTO dashboardDTO = new DashboardDTO(
                saldoGeral,
                saldoPeriodo,
                totalInvestido,
                gastoDTO,
                Collections.emptyList(),
                recebimentoDTO,
                Collections.emptyList(),
                MOCK_USER_ID
        );

        when(relatorioService.getDashboard(
                eq(MOCK_USER_ID),
                any(Integer.class),
                eq(INICIO_PERIODO),
                eq(FIM_PERIODO)))
                .thenReturn(dashboardDTO);

        mockMvc.perform(get("/api/dashboard")
                        .param("inicio", INICIO_PERIODO.toString())
                        .param("fim", FIM_PERIODO.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoPeriodo").value(saldoPeriodo.toString()))
                .andExpect(jsonPath("$.saldoGeral").value(saldoGeral.toString()))
                .andExpect(jsonPath("$.totalInvestido").value(totalInvestido.toString()))
                .andExpect(jsonPath("$.ultimosGastos").isArray())
                .andExpect(jsonPath("$.ultimosRecebimentos").isArray());

        verify(relatorioService).getDashboard(
                eq(MOCK_USER_ID),
                eq(LIMITE_PADRAO),
                eq(INICIO_PERIODO),
                eq(FIM_PERIODO));
    }

    @Test
    @DisplayName("GET /api/dashboard - Deve retornar valores zero quando não houver dados")
    void buscarTodos_QuandoSemDados_DeveRetornarZero() throws Exception {
        DashboardDTO zeroDTO = new DashboardDTO(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                MOCK_USER_ID
        );

        when(relatorioService.getDashboard(
                any(Long.class),
                any(Integer.class),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(zeroDTO);

        mockMvc.perform(get("/api/dashboard")
                        .param("inicio", INICIO_PERIODO.toString())
                        .param("fim", FIM_PERIODO.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoPeriodo").value("0"))
                .andExpect(jsonPath("$.saldoGeral").value("0"))
                .andExpect(jsonPath("$.totalInvestido").value("0"));
    }
}