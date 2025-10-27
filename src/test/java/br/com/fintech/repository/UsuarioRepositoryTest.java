package br.com.fintech.repository;

import br.com.fintech.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final String EMAIL_EXISTENTE = "teste@fintech.com";
    private static final String EMAIL_NAO_EXISTENTE = "naoexiste@fintech.com";
    private Usuario usuarioPadrao;

    @BeforeEach
    void setUp() {
        usuarioPadrao = new Usuario();
        usuarioPadrao.setNome("Usuario Teste");
        usuarioPadrao.setEmail(EMAIL_EXISTENTE);
        usuarioPadrao.setSenha("$2a$10$HASHED_PASSWORD_MOCK");

        entityManager.persistAndFlush(usuarioPadrao);
    }

    // ----------------------------------------------------
    // TESTE: findByEmail
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve encontrar o usuário pelo email quando ele existe")
    void findByEmail_QuandoExiste_DeveRetornarUsuario() {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail(EMAIL_EXISTENTE);

        assertTrue(encontrado.isPresent());
        assertEquals(EMAIL_EXISTENTE, encontrado.get().getEmail());
        assertEquals(usuarioPadrao.getNome(), encontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o email não existe")
    void findByEmail_QuandoNaoExiste_DeveRetornarOptionalVazio() {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail(EMAIL_NAO_EXISTENTE);

        assertFalse(encontrado.isPresent());
    }
}