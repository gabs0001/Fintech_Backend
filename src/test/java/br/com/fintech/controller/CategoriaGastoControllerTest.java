package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.service.CategoriaGastoService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoriaGastoControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CategoriaGastoService categoriaGastoService;

    @InjectMocks
    private CategoriaGastoController categoriaGastoController;

    private static final Long CATEGORIA_ID = 5L;
    private CategoriaGasto categoriaValida;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaGastoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        categoriaValida = new CategoriaGasto();
        categoriaValida.setId(CATEGORIA_ID);
        categoriaValida.setDescricao("Alimentação");
    }

    // ----------------------------------------------------
    // TESTES DE GET (Buscar)
    // ----------------------------------------------------

    @Test
    @DisplayName("GET /api/categorias-gastos/{id} - Deve retornar 200 OK e a Categoria se encontrada")
    void buscarPorId_QuandoEncontrado_DeveRetornar200OK() throws Exception {
        when(categoriaGastoService.getById(CATEGORIA_ID)).thenReturn(categoriaValida);

        mockMvc.perform(get("/api/categorias-gastos/{id}", CATEGORIA_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(CATEGORIA_ID))
                .andExpect(jsonPath("$.descricao").value("Alimentação"));
    }

    @Test
    @DisplayName("GET /api/categorias-gastos/{id} - Deve retornar 404 Not Found se não encontrado")
    void buscarPorId_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        when(categoriaGastoService.getById(CATEGORIA_ID))
                .thenThrow(new EntityNotFoundException("Categoria não encontrada."));

        mockMvc.perform(get("/api/categorias-gastos/{id}", CATEGORIA_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/categorias-gastos - Deve retornar 200 OK e uma lista de Categorias")
    void buscarTodos_DeveRetornar200OKComLista() throws Exception {
        CategoriaGasto outraCategoria = new CategoriaGasto();
        outraCategoria.setId(6L);
        outraCategoria.setDescricao("Transporte");

        when(categoriaGastoService.getAll()).thenReturn(List.of(categoriaValida, outraCategoria));

        mockMvc.perform(get("/api/categorias-gastos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].descricao").value("Alimentação"));
    }

    // ----------------------------------------------------
    // TESTES DE POST (Criar)
    // ----------------------------------------------------

    @Test
    @DisplayName("POST /api/categorias-gastos - Deve retornar 201 Created e a Categoria criada")
    void salvar_QuandoCategoriaValida_DeveRetornar201Created() throws Exception {
        CategoriaGasto categoriaInput = new CategoriaGasto();
        categoriaInput.setDescricao("Viagem");

        when(categoriaGastoService.insert(any(CategoriaGasto.class))).thenAnswer(invocation -> {
            CategoriaGasto categoriaSalva = invocation.getArgument(0);
            categoriaSalva.setId(10L);
            return categoriaSalva;
        });

        mockMvc.perform(post("/api/categorias-gastos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.descricao").value("Viagem"));
    }

    @Test
    @DisplayName("POST /api/categorias-gastos - Deve retornar 400 Bad Request se a validação falhar")
    void salvar_QuandoCategoriaInvalida_DeveRetornar400BadRequest() throws Exception {
        CategoriaGasto categoriaInputInvalida = new CategoriaGasto();
        categoriaInputInvalida.setDescricao("");

        when(categoriaGastoService.insert(any(CategoriaGasto.class)))
                .thenThrow(new IllegalArgumentException("Nome da categoria é obrigatório!"));

        mockMvc.perform(post("/api/categorias-gastos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaInputInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ----------------------------------------------------
    // TESTES DE PUT (Atualizar)
    // ----------------------------------------------------

    @Test
    @DisplayName("PUT /api/categorias-gastos/{id} - Deve retornar 200 OK após atualização")
    void atualizar_QuandoValido_DeveRetornar200OK() throws Exception {
        CategoriaGasto categoriaAtualizada = categoriaValida;
        categoriaAtualizada.setDescricao("Alimentação Atualizada");

        when(categoriaGastoService.update(eq(CATEGORIA_ID), any(CategoriaGasto.class))).thenReturn(categoriaAtualizada);

        mockMvc.perform(put("/api/categorias-gastos/{id}", CATEGORIA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Alimentação Atualizada"));
    }

    // ----------------------------------------------------
    // TESTES DE DELETE (Remover)
    // ----------------------------------------------------

    @Test
    @DisplayName("DELETE /api/categorias-gastos/{id} - Deve retornar 204 No Content se removido com sucesso")
    void remover_QuandoEncontrado_DeveRetornar204NoContent() throws Exception {
        mockMvc.perform(delete("/api/categorias-gastos/{id}", CATEGORIA_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/categorias-gastos/{id} - Deve retornar 404 Not Found se não encontrado")
    void remover_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Categoria não encontrada para remoção."))
                .when(categoriaGastoService).remove(CATEGORIA_ID);

        mockMvc.perform(delete("/api/categorias-gastos/{id}", CATEGORIA_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}