package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.RecebimentoService;
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
class RecebimentoControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private RecebimentoService recebimentoService;

    @InjectMocks
    private RecebimentoController recebimentoController;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long RECEBIMENTO_ID = 10L;
    private Recebimento recebimentoValido;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recebimentoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        recebimentoValido = new Recebimento();
        recebimentoValido.setId(RECEBIMENTO_ID);
        recebimentoValido.setUsuarioId(MOCK_USER_ID);
        recebimentoValido.setDescricao("Salário Teste");
        recebimentoValido.setValor(new BigDecimal("5000.00"));
        recebimentoValido.setDataRecebimento(LocalDate.now());
    }

    @Test
    @DisplayName("GET /api/recebimentos/{id} - Deve retornar 200 OK e o Recebimento se encontrado")
    void buscarPorId_QuandoEncontrado_DeveRetornar200OK() throws Exception {
        when(recebimentoService.getById(RECEBIMENTO_ID, MOCK_USER_ID)).thenReturn(recebimentoValido);

        mockMvc.perform(get("/api/recebimentos/{id}", RECEBIMENTO_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(RECEBIMENTO_ID))
                .andExpect(jsonPath("$.descricao").value("Salário Teste"));
    }

    @Test
    @DisplayName("GET /api/recebimentos/{id} - Deve retornar 404 Not Found se não encontrado")
    void buscarPorId_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        when(recebimentoService.getById(RECEBIMENTO_ID, MOCK_USER_ID))
                .thenThrow(new EntityNotFoundException("Recebimento não encontrado."));

        mockMvc.perform(get("/api/recebimentos/{id}", RECEBIMENTO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/recebimentos - Deve retornar 200 OK e lista vazia se não houver")
    void buscarTodos_QuandoNaoHouver_DeveRetornar200OKListaVazia() throws Exception {
        when(recebimentoService.findAllByOwnerId(MOCK_USER_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recebimentos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /api/recebimentos - Deve retornar 201 Created e o Recebimento criado")
    void salvar_QuandoRecebimentoValido_DeveRetornar201Created() throws Exception {
        Recebimento recebimentoInput = new Recebimento();
        recebimentoInput.setDescricao("Novo Recebimento POST");
        recebimentoInput.setValor(new BigDecimal("100.00"));
        recebimentoInput.setDataRecebimento(LocalDate.now());

        when(recebimentoService.insert(any(Recebimento.class))).thenAnswer(invocation -> {
            Recebimento recebimentoSalvo = invocation.getArgument(0);
            recebimentoSalvo.setId(101L);
            recebimentoSalvo.setUsuarioId(MOCK_USER_ID);
            return recebimentoSalvo;
        });

        mockMvc.perform(post("/api/recebimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recebimentoInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.usuarioId").value(MOCK_USER_ID));
    }

    @Test
    @DisplayName("POST /api/recebimentos - Deve retornar 400 Bad Request se a validação falhar")
    void salvar_QuandoRecebimentoInvalido_DeveRetornar400BadRequest() throws Exception {
        Recebimento recebimentoInputInvalido = new Recebimento();
        recebimentoInputInvalido.setValor(BigDecimal.ZERO);
        recebimentoInputInvalido.setDataRecebimento(LocalDate.now());

        when(recebimentoService.insert(any(Recebimento.class)))
                .thenThrow(new IllegalArgumentException("Erro: O valor do recebimento deve ser maior que zero!"));

        mockMvc.perform(post("/api/recebimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recebimentoInputInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("DELETE /api/recebimentos/{id} - Deve retornar 204 No Content se removido com sucesso")
    void remover_QuandoEncontrado_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/recebimentos/{id}", RECEBIMENTO_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/recebimentos/{id} - Deve retornar 404 Not Found se não encontrado")
    void remover_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Recebimento não encontrado para remoção."))
                .when(recebimentoService).remove(RECEBIMENTO_ID, MOCK_USER_ID);

        mockMvc.perform(delete("/api/recebimentos/{id}", RECEBIMENTO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}