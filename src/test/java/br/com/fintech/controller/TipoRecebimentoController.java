package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.TipoRecebimento;
import br.com.fintech.service.TipoRecebimentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TipoRecebimentoController {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TipoRecebimentoService tipoRecebimentoService;

    @InjectMocks
    private TipoRecebimentoController tipoRecebimentoController;

    private static final Long CATEGORIA_ID = 5L;
    private TipoRecebimento categoriaValida;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tipoRecebimentoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        categoriaValida = new TipoRecebimento();
        categoriaValida.setId(CATEGORIA_ID);
        categoriaValida.setDescricao("Aluguel");
        categoriaValida.setDescricao("Aluguel mensal recebido da casa número 1");
    }

    // ----------------------------------------------------
    // TESTES DE GET (Buscar)
    // ----------------------------------------------------

    @Test
    @DisplayName("GET /api/tipos-recebimentos/{id} - Deve retornar 200 OK e a Categoria se encontrada")
    void buscarPorId_QuandoEncontrado_DeveRetornar200OK() throws Exception {
        when(tipoRecebimentoService.getById(CATEGORIA_ID)).thenReturn(categoriaValida);

        mockMvc.perform(get("/api/tipos-recebimentos/{id}", CATEGORIA_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CATEGORIA_ID))
                .andExpect(jsonPath("$.nome").value("Aluguel"));
    }

    @Test
    @DisplayName("GET /api/tipos-recebimentos/{id} - Deve retornar 404 Not Found se não encontrado")
    void buscarPorId_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        when(tipoRecebimentoService.getById(CATEGORIA_ID))
                .thenThrow(new EntityNotFoundException("Categoria não encontrada."));

        mockMvc.perform(get("/api/tipos-recebimentos/{id}", CATEGORIA_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Categoria não encontrada."));
    }

    @Test
    @DisplayName("GET /api/tipos-recebimentos - Deve retornar 200 OK e uma lista de Categorias")
    void buscarTodos_DeveRetornar200OKComLista() throws Exception {
        TipoRecebimento outraCategoria = new TipoRecebimento();
        outraCategoria.setId(6L);
        outraCategoria.setDescricao("Salário");

        when(tipoRecebimentoService.getAll()).thenReturn(List.of(categoriaValida, outraCategoria));

        mockMvc.perform(get("/api/tipos-recebimentos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Aluguel"));
    }

    // ----------------------------------------------------
    // TESTES DE POST (Criar)
    // ----------------------------------------------------

    @Test
    @DisplayName("POST /api/tipos-recebimentos - Deve retornar 201 Created e a Categoria criada")
    void salvar_QuandoCategoriaValida_DeveRetornar201Created() throws Exception {
        TipoRecebimento categoriaInput = new TipoRecebimento();
        categoriaInput.setDescricao("Auxílio Mensal");

        when(tipoRecebimentoService.insert(any(TipoRecebimento.class))).thenAnswer(invocation -> {
            TipoRecebimento categoriaSalva = invocation.getArgument(0);
            categoriaSalva.setId(10L);
            return categoriaSalva;
        });

        mockMvc.perform(post("/api/tipos-recebimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("Auxílio Mensal"));
    }

    @Test
    @DisplayName("POST /api/tipos-recebimentos - Deve retornar 400 Bad Request se a validação falhar")
    void salvar_QuandoCategoriaInvalida_DeveRetornar400BadRequest() throws Exception {
        TipoRecebimento categoriaInputInvalida = new TipoRecebimento();
        categoriaInputInvalida.setDescricao("");

        when(tipoRecebimentoService.insert(any(TipoRecebimento.class)))
                .thenThrow(new IllegalArgumentException("Nome da categoria é obrigatório!"));

        mockMvc.perform(post("/api/tipos-recebimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInputInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Nome da categoria é obrigatório!"));
    }

    // ----------------------------------------------------
    // TESTES DE PUT (Atualizar)
    // ----------------------------------------------------

    @Test
    @DisplayName("PUT /api/tipos-recebimentos/{id} - Deve retornar 200 OK após atualização")
    void atualizar_QuandoValido_DeveRetornar200OK() throws Exception {
        TipoRecebimento categoriaAtualizada = categoriaValida;
        categoriaAtualizada.setDescricao("Aluguel Atualizada");

        when(tipoRecebimentoService.update(eq(CATEGORIA_ID), any(TipoRecebimento.class))).thenReturn(categoriaAtualizada);

        mockMvc.perform(put("/api/tipos-recebimentos/{id}", CATEGORIA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Aluguel Atualizada"));
    }

    // ----------------------------------------------------
    // TESTES DE DELETE (Remover)
    // ----------------------------------------------------

    @Test
    @DisplayName("DELETE /api/tipos-recebimentos/{id} - Deve retornar 204 No Content se removido com sucesso")
    void remover_QuandoEncontrado_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/tipos-recebimentos/{id}", CATEGORIA_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tipos-recebimentos/{id} - Deve retornar 404 Not Found se não encontrado")
    void remover_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Categoria não encontrada para remoção."))
                .when(tipoRecebimentoService).remove(CATEGORIA_ID);

        mockMvc.perform(delete("/api/tipos-recebimentos/{id}", CATEGORIA_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}