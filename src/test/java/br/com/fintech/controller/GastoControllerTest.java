package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.Gasto;
import br.com.fintech.service.GastoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GastoControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GastoService gastoService;

    @InjectMocks
    private GastoController gastoController;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long GASTO_ID = 10L;
    private Gasto gastoValido;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gastoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        gastoValido = new Gasto();
        gastoValido.setId(GASTO_ID);
        gastoValido.setUsuarioId(MOCK_USER_ID);
        gastoValido.setDescricao("Gasto Teste Integração");
        gastoValido.setValor(new BigDecimal("150.00"));
        gastoValido.setDataGasto(LocalDate.now());
    }

    @Test
    @DisplayName("GET /api/gastos/{id} - Deve retornar 200 OK e o Gasto se encontrado")
    void buscarPorId_QuandoEncontrado_DeveRetornar200OK() throws Exception {
        when(gastoService.getById(GASTO_ID, MOCK_USER_ID)).thenReturn(gastoValido);

        mockMvc.perform(get("/api/gastos/{id}", GASTO_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(GASTO_ID))
                .andExpect(jsonPath("$.descricao").value("Gasto Teste Integração"));
    }

    @Test
    @DisplayName("GET /api/gastos/{id} - Deve retornar 404 Not Found se não encontrado")
    void buscarPorId_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        when(gastoService.getById(GASTO_ID, MOCK_USER_ID))
                .thenThrow(new EntityNotFoundException("Gasto não encontrado."));

        mockMvc.perform(get("/api/gastos/{id}", GASTO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/gastos - Deve retornar 201 Created e o Gasto criado")
    void salvar_QuandoGastoValido_DeveRetornar201Created() throws Exception {
        Gasto gastoInput = new Gasto();
        gastoInput.setDescricao("Novo Gasto POST");
        gastoInput.setValor(new BigDecimal("99.00"));
        gastoInput.setDataGasto(LocalDate.now());

        when(gastoService.insert(any(Gasto.class))).thenAnswer(invocation -> {
            Gasto gastoSalvo = invocation.getArgument(0);
            gastoSalvo.setId(101L);
            gastoSalvo.setUsuarioId(MOCK_USER_ID);
            return gastoSalvo;
        });

        mockMvc.perform(post("/api/gastos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gastoInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.usuarioId").value(MOCK_USER_ID));
    }

    @Test
    @DisplayName("POST /api/gastos - Deve retornar 400 Bad Request se a validação falhar")
    void salvar_QuandoGastoInvalido_DeveRetornar400BadRequest() throws Exception {
        Gasto gastoInputInvalido = new Gasto();
        gastoInputInvalido.setValor(BigDecimal.ZERO);
        gastoInputInvalido.setDataGasto(LocalDate.now());

        when(gastoService.insert(any(Gasto.class)))
                .thenThrow(new IllegalArgumentException("Erro: O valor do gasto deve ser maior que zero!"));

        mockMvc.perform(post("/api/gastos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gastoInputInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("DELETE /api/gastos/{id} - Deve retornar 404 Not Found se não encontrado")
    void remover_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Gasto não encontrado para remoção."))
                .when(gastoService).remove(GASTO_ID, MOCK_USER_ID);

        mockMvc.perform(delete("/api/gastos/{id}", GASTO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}