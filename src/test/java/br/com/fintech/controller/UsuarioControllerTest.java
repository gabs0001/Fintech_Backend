package br.com.fintech.controller;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // ----------------------------------------------------
    // TESTE: POST /api/usuarios (Cadastro)
    // ----------------------------------------------------

    @Test
    @DisplayName("POST /api/usuarios - Deve retornar 201 Created e o usuário criado")
    void salvarUsuario_QuandoValido_DeveRetornar201Created() throws Exception {
        Usuario usuarioInput = new Usuario();
        usuarioInput.setNome("Novo User");
        usuarioInput.setEmail("novo@email.com");
        usuarioInput.setSenha("SenhaSegura123");

        when(usuarioService.insert(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            usuarioSalvo.setId(10L);
            usuarioSalvo.setSenha("HASH_MOCK");
            return usuarioSalvo;
        });

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    @DisplayName("POST /api/usuarios - Deve retornar 400 Bad Request se o email for inválido ou duplicado")
    void salvarUsuario_QuandoInvalido_DeveRetornar400BadRequest() throws Exception {
        Usuario usuarioInputInvalido = new Usuario();
        usuarioInputInvalido.setNome("Invalido");
        usuarioInputInvalido.setEmail("email_invalido");
        usuarioInputInvalido.setSenha("123");

        when(usuarioService.insert(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Formato de e-mail inválido."));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInputInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Formato de e-mail inválido."));
    }
}