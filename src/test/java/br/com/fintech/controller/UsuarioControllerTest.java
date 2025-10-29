package br.com.fintech.controller;

import br.com.fintech.dto.ChangeEmailRequest;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.exceptions.GlobalExceptionHandler;
import br.com.fintech.model.Usuario;
import br.com.fintech.service.UsuarioService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private static final Long MOCK_USER_ID = 1L;
    private Usuario mockUsuario;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        mockUsuario = new Usuario();
        mockUsuario.setId(MOCK_USER_ID);
        mockUsuario.setNome("Test User");
        mockUsuario.setEmail("test@email.com");
        mockUsuario.setSenha("HASH_MOCK");
    }

    // ----------------------------------------------------
    // TESTE: GET /api/usuarios/me
    // ----------------------------------------------------

    @Test
    @DisplayName("GET /api/usuarios/me - Deve retornar 200 OK e o perfil do usuário")
    void buscarPerfil_DeveRetornar200OK() throws Exception {
        when(usuarioService.getById(eq(MOCK_USER_ID), eq(MOCK_USER_ID))).thenReturn(mockUsuario);

        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(MOCK_USER_ID))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    // ----------------------------------------------------
    // TESTE: PATCH /api/usuarios/email
    // ----------------------------------------------------

    @Test
    @DisplayName("PATCH /api/usuarios/email - Deve retornar 200 OK após alteração de email")
    void alterarEmail_QuandoValido_DeveRetornar200OK() throws Exception {
        String novoEmail = "novo.test@email.com";

        Usuario usuarioAtualizado = mockUsuario;
        usuarioAtualizado.setEmail(novoEmail);

        ChangeEmailRequest request = new ChangeEmailRequest(novoEmail);

        when(usuarioService.changeEmail(eq(MOCK_USER_ID), eq(MOCK_USER_ID), eq(novoEmail)))
                .thenReturn(usuarioAtualizado);

        mockMvc.perform(patch("/api/usuarios/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(novoEmail));
    }

    // ----------------------------------------------------
    // TESTE: DELETE /api/usuarios/me
    // ----------------------------------------------------

    @Test
    @DisplayName("DELETE /api/usuarios/me - Deve retornar 204 No Content após remoção")
    void removerConta_DeveRetornar204NoContent() throws Exception {
        doNothing().when(usuarioService).remove(MOCK_USER_ID);

        mockMvc.perform(delete("/api/usuarios/me"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/me - Deve retornar 404 Not Found se usuário não existir")
    void removerConta_QuandoNaoEncontrado_DeveRetornar404NotFound() throws Exception {
        doThrow(new EntityNotFoundException("Usuário não encontrado")).when(usuarioService).remove(MOCK_USER_ID);

        mockMvc.perform(delete("/api/usuarios/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}