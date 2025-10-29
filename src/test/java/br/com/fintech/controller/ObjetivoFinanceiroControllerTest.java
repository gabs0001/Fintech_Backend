package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.ObjetivoFinanceiro;
import br.com.fintech.service.ObjetivoFinanceiroService;
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
class ObjetivoFinanceiroControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ObjetivoFinanceiroService objetivoFinanceiroService;

    @InjectMocks
    private ObjetivoFinanceiroController objetivoFinanceiroController;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long OBJETIVO_ID = 20L;
    private ObjetivoFinanceiro objetivoValido;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(objetivoFinanceiroController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        objetivoValido = new ObjetivoFinanceiro();
        objetivoValido.setId(OBJETIVO_ID);
        objetivoValido.setUsuarioId(MOCK_USER_ID);
        objetivoValido.setDescricao("Comprar Casa");
        objetivoValido.setValor(new BigDecimal("300000.00"));
        objetivoValido.setDataConclusao(LocalDate.now().plusYears(10));
    }

    @Test
    @DisplayName("GET /api/objetivos-financeiros/{id} - Deve retornar 200 OK e o Objetivo se encontrado")
    void buscarPorId_QuandoEncontrado_DeveRetornar200OK() throws Exception {
        when(objetivoFinanceiroService.getById(OBJETIVO_ID, MOCK_USER_ID)).thenReturn(objetivoValido);

        mockMvc.perform(get("/api/objetivos-financeiros/{id}", OBJETIVO_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(OBJETIVO_ID))
                .andExpect(jsonPath("$.descricao").value("Comprar Casa"));
    }

    @Test
    @DisplayName("GET /api/objetivos-financeiros/{id} - Deve retornar 404 Not Found se não encontrado")
    void buscarPorId_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        when(objetivoFinanceiroService.getById(OBJETIVO_ID, MOCK_USER_ID))
                .thenThrow(new EntityNotFoundException("Objetivo não encontrado."));

        mockMvc.perform(get("/api/objetivos-financeiros/{id}", OBJETIVO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/objetivos-financeiros - Deve retornar 200 OK e lista vazia se não houver")
    void buscarTodos_QuandoNaoHouver_DeveRetornar200OKListaVazia() throws Exception {
        when(objetivoFinanceiroService.findAllByOwnerId(MOCK_USER_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/objetivos-financeiros"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /api/objetivos-financeiros - Deve retornar 201 Created e o Objetivo criado")
    void salvar_QuandoObjetivoValido_DeveRetornar201Created() throws Exception {
        ObjetivoFinanceiro objetivoInput = new ObjetivoFinanceiro();
        objetivoInput.setDescricao("Viajar para Europa");
        objetivoInput.setValor(new BigDecimal("15000.00"));
        objetivoInput.setDataConclusao(LocalDate.now().plusYears(1));

        when(objetivoFinanceiroService.insert(any(ObjetivoFinanceiro.class))).thenAnswer(invocation -> {
            ObjetivoFinanceiro objetivoSalvo = invocation.getArgument(0);
            objetivoSalvo.setId(101L);
            objetivoSalvo.setUsuarioId(MOCK_USER_ID);
            return objetivoSalvo;
        });

        mockMvc.perform(post("/api/objetivos-financeiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(objetivoInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.descricao").value("Viajar para Europa"))
                .andExpect(jsonPath("$.usuarioId").value(MOCK_USER_ID));
    }

    @Test
    @DisplayName("POST /api/objetivos - Deve retornar 400 Bad Request se a validação falhar")
    void salvar_QuandoObjetivoInvalido_DeveRetornar400BadRequest() throws Exception {
        ObjetivoFinanceiro objetivoInputInvalido = new ObjetivoFinanceiro();
        objetivoInputInvalido.setValor(BigDecimal.ZERO);
        objetivoInputInvalido.setDataConclusao(LocalDate.now().plusYears(1));

        when(objetivoFinanceiroService.insert(any(ObjetivoFinanceiro.class)))
                .thenThrow(new IllegalArgumentException("Erro: O valor do objetivo deve ser maior que zero!"));

        mockMvc.perform(post("/api/objetivos-financeiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(objetivoInputInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("DELETE /api/objetivos/{id} - Deve retornar 204 No Content se removido com sucesso")
    void remover_QuandoEncontrado_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/objetivos-financeiros/{id}", OBJETIVO_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/objetivos/{id} - Deve retornar 404 Not Found se não encontrado")
    void remover_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Objetivo não encontrado para remoção."))
                .when(objetivoFinanceiroService).deleteByIdAndOwnerId(OBJETIVO_ID, MOCK_USER_ID);

        mockMvc.perform(delete("/api/objetivos-financeiros/{id}", OBJETIVO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}