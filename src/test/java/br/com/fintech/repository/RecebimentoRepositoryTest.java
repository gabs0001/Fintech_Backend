package br.com.fintech.repository;

import br.com.fintech.model.Recebimento;
import br.com.fintech.model.TipoRecebimento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class RecebimentoRepositoryTest {
    @Autowired
    private RecebimentoRepository recebimentoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        TipoRecebimento tipo = new TipoRecebimento();
        tipo.setDescricao("Salário");
        entityManager.persistAndFlush(tipo);

        Recebimento rec1 = new Recebimento();
        rec1.setUsuarioId(MOCK_USER_ID);
        rec1.setValor(new BigDecimal("3000.00"));
        rec1.setDataRecebimento(LocalDate.of(2025, 3, 5));
        rec1.setTipoRecebimento(tipo);
        entityManager.persistAndFlush(rec1);

        Recebimento rec2 = new Recebimento();
        rec2.setUsuarioId(MOCK_USER_ID);
        rec2.setValor(new BigDecimal("1500.00"));
        rec2.setDataRecebimento(LocalDate.of(2025, 4, 10));
        rec2.setTipoRecebimento(tipo);
        entityManager.persistAndFlush(rec2);

        Recebimento rec3 = new Recebimento();
        rec3.setUsuarioId(MOCK_USER_ID);
        rec3.setValor(new BigDecimal("500.00"));
        rec3.setDataRecebimento(LocalDate.of(2025, 4, 25));
        rec3.setTipoRecebimento(tipo);
        entityManager.persistAndFlush(rec3);

        Recebimento recOutroUser = new Recebimento();
        recOutroUser.setUsuarioId(OTHER_USER_ID);
        recOutroUser.setValor(new BigDecimal("2000.00"));
        recOutroUser.setDataRecebimento(LocalDate.of(2025, 4, 15));
        recOutroUser.setTipoRecebimento(tipo);
        entityManager.persistAndFlush(recOutroUser);

        Recebimento recForaPeriodo = new Recebimento();
        recForaPeriodo.setUsuarioId(MOCK_USER_ID);
        recForaPeriodo.setValor(new BigDecimal("1000.00"));
        recForaPeriodo.setDataRecebimento(LocalDate.of(2024, 12, 1));
        recForaPeriodo.setTipoRecebimento(tipo);
        entityManager.persistAndFlush(recForaPeriodo);
    }

    // ----------------------------------------------------
    // TESTE: calcularTotal
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve calcular o total de recebimentos de todos os tempos para um usuário específico")
    void calcularTotal_DeveRetornarTotalCorretoParaUsuario() {
        // Total esperado: 3000 + 1500 + 500 + 1000 = 6000.00
        BigDecimal totalEsperado = new BigDecimal("6000.00");

        BigDecimal totalCalculado = recebimentoRepository.calcularTotal(MOCK_USER_ID);

        assertNotNull(totalCalculado);
        assertEquals(0, totalEsperado.compareTo(totalCalculado));
    }

    @Test
    @DisplayName("Deve retornar zero se o usuário não tiver recebimentos")
    void calcularTotal_DeveRetornarZeroParaUsuarioSemRecebimentos() {
        BigDecimal totalCalculado = recebimentoRepository.calcularTotal(99L);

        assertNotNull(totalCalculado);
        assertEquals(BigDecimal.ZERO, totalCalculado);
    }

    // ----------------------------------------------------
    // TESTE: calcularTotalPeriodo
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve calcular o total de recebimentos dentro do período especificado para o usuário")
    void calcularTotalPeriodo_DeveCalcularCorretamente() {
        LocalDate inicio = LocalDate.of(2025, 4, 1);
        LocalDate fim = LocalDate.of(2025, 4, 30);

        BigDecimal totalEsperado = new BigDecimal("2000.00");

        BigDecimal totalCalculado = recebimentoRepository.calcularTotalPeriodo(MOCK_USER_ID, inicio, fim);

        assertNotNull(totalCalculado);
        assertEquals(0, totalEsperado.compareTo(totalCalculado));
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver recebimentos no período")
    void calcularTotalPeriodo_DeveRetornarZeroQuandoVazio() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);

        BigDecimal totalCalculado = recebimentoRepository.calcularTotalPeriodo(MOCK_USER_ID, inicio, fim);

        assertNotNull(totalCalculado);
        assertEquals(BigDecimal.ZERO, totalCalculado);
    }

    @Test
    @DisplayName("Deve ignorar recebimentos de outros usuários no período")
    void calcularTotalPeriodo_DeveIgnorarOutrosUsuarios() {
        LocalDate inicio = LocalDate.of(2025, 4, 1);
        LocalDate fim = LocalDate.of(2025, 4, 30);

        BigDecimal totalCalculado = recebimentoRepository.calcularTotalPeriodo(OTHER_USER_ID, inicio, fim);

        assertNotNull(totalCalculado);
        assertEquals(0, new BigDecimal("2000.00").compareTo(totalCalculado));

        BigDecimal totalCalculadoMock = recebimentoRepository.calcularTotalPeriodo(MOCK_USER_ID, inicio, fim);
        assertEquals(0, new BigDecimal("2000.00").compareTo(totalCalculadoMock));
    }
}