package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.Investimento;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InvestimentoControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private InvestimentoService investimentoService;

    @InjectMocks
    private InvestimentoController investimentoController;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long INVESTIMENTO_ID = 10L;
    private Investimento investimentoValido;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(investimentoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        investimentoValido = new Investimento();
        investimentoValido.setId(INVESTIMENTO_ID);
        investimentoValido.setUsuarioId(MOCK_USER_ID);
        investimentoValido.setNome("CDB Teste");
        investimentoValido.setDescricao("Investimento Teste");
        investimentoValido.setValor(new BigDecimal("1000.00"));
        investimentoValido.setDataRealizacao(LocalDate.now());
    }

    @Test
    @DisplayName("GET /api/investimentos/{id} - Deve retornar 200 OK e o Investimento se encontrado")
    void buscarPorId_QuandoEncontrado_DeveRetornar200OK() throws Exception {
        when(investimentoService.getById(INVESTIMENTO_ID, MOCK_USER_ID)).thenReturn(investimentoValido);

        mockMvc.perform(get("/api/investimentos/{id}", INVESTIMENTO_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(INVESTIMENTO_ID))
                .andExpect(jsonPath("$.nome").value("CDB Teste"));
    }

    @Test
    @DisplayName("GET /api/investimentos/{id} - Deve retornar 404 Not Found se não encontrado")
    void buscarPorId_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        when(investimentoService.getById(INVESTIMENTO_ID, MOCK_USER_ID))
                .thenThrow(new EntityNotFoundException("Investimento não encontrado."));

        mockMvc.perform(get("/api/investimentos/{id}", INVESTIMENTO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/investimentos - Deve retornar 200 OK e lista vazia se não houver")
    void buscarTodos_QuandoNaoHouver_DeveRetornar200OKListaVazia() throws Exception {
        when(investimentoService.findAllByOwnerId(MOCK_USER_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/investimentos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /api/investimentos - Deve retornar 201 Created e o Investimento criado")
    void salvar_QuandoInvestimentoValido_DeveRetornar201Created() throws Exception {
        Investimento investimentoInput = new Investimento();
        investimentoInput.setNome("Novo Investimento POST");
        investimentoInput.setValor(new BigDecimal("500.00"));
        investimentoInput.setDataRealizacao(LocalDate.now());

        when(investimentoService.insert(any(Investimento.class))).thenAnswer(invocation -> {
            Investimento investimentoSalvo = invocation.getArgument(0);
            investimentoSalvo.setId(101L);
            investimentoSalvo.setUsuarioId(MOCK_USER_ID);
            return investimentoSalvo;
        });

        mockMvc.perform(post("/api/investimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investimentoInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.usuarioId").value(MOCK_USER_ID));
    }

    @Test
    @DisplayName("POST /api/investimentos - Deve retornar 400 Bad Request se a validação falhar")
    void salvar_QuandoInvestimentoInvalido_DeveRetornar400BadRequest() throws Exception {
        Investimento investimentoInputInvalido = new Investimento();
        investimentoInputInvalido.setValor(BigDecimal.ZERO);
        investimentoInputInvalido.setDataRealizacao(LocalDate.now());

        when(investimentoService.insert(any(Investimento.class)))
                .thenThrow(new IllegalArgumentException("Erro: o valor do investimento deve ser maior que zero!"));

        mockMvc.perform(post("/api/investimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investimentoInputInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("DELETE /api/investimentos/{id} - Deve retornar 204 No Content se removido com sucesso")
    void remover_QuandoEncontrado_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/investimentos/{id}", INVESTIMENTO_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/investimentos/{id} - Deve retornar 404 Not Found se não encontrado")
    void remover_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Investimento não encontrado para remoção."))
                .when(investimentoService).remove(INVESTIMENTO_ID, MOCK_USER_ID);

        mockMvc.perform(delete("/api/investimentos/{id}", INVESTIMENTO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}