package br.com.fintech.repository;

import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.model.Gasto;
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
class GastoRepositoryTest {
    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        CategoriaGasto categoria = new CategoriaGasto();
        categoria.setDescricao("TesteCategoria");
        entityManager.persistAndFlush(categoria);

        Gasto gasto1 = new Gasto();
        gasto1.setUsuarioId(MOCK_USER_ID);
        gasto1.setValor(new BigDecimal("100.00"));
        gasto1.setDataGasto(LocalDate.of(2025, 3, 15));
        gasto1.setCategoriaGasto(categoria);
        entityManager.persistAndFlush(gasto1);

        Gasto gasto2 = new Gasto();
        gasto2.setUsuarioId(MOCK_USER_ID);
        gasto2.setValor(new BigDecimal("200.00"));
        gasto2.setDataGasto(LocalDate.of(2025, 4, 1));
        gasto2.setCategoriaGasto(categoria);
        entityManager.persistAndFlush(gasto2);

        Gasto gasto3 = new Gasto();
        gasto3.setUsuarioId(MOCK_USER_ID);
        gasto3.setValor(new BigDecimal("50.00"));
        gasto3.setDataGasto(LocalDate.of(2025, 4, 20));
        gasto3.setCategoriaGasto(categoria);
        entityManager.persistAndFlush(gasto3);

        Gasto gastoOutroUser = new Gasto();
        gastoOutroUser.setUsuarioId(OTHER_USER_ID);
        gastoOutroUser.setValor(new BigDecimal("1000.00"));
        gastoOutroUser.setDataGasto(LocalDate.of(2025, 4, 15));
        gastoOutroUser.setCategoriaGasto(categoria);
        entityManager.persistAndFlush(gastoOutroUser);

        Gasto gastoForaPeriodo = new Gasto();
        gastoForaPeriodo.setUsuarioId(MOCK_USER_ID);
        gastoForaPeriodo.setValor(new BigDecimal("500.00"));
        gastoForaPeriodo.setDataGasto(LocalDate.of(2024, 1, 1));
        gastoForaPeriodo.setCategoriaGasto(categoria);
        entityManager.persistAndFlush(gastoForaPeriodo);
    }

    // ----------------------------------------------------
    // TESTE: calcularTotal
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve calcular o total de gastos de todos os tempos para um usuário específico")
    void calcularTotal_DeveRetornarTotalCorretoParaUsuario() {
        BigDecimal totalEsperado = new BigDecimal("850.00");

        BigDecimal totalCalculado = gastoRepository.calcularTotal(MOCK_USER_ID);

        assertNotNull(totalCalculado);
        assertEquals(0, totalEsperado.compareTo(totalCalculado), "O total calculado deve ser 850.00");
    }

    @Test
    @DisplayName("Deve retornar zero se o usuário não tiver gastos")
    void calcularTotal_DeveRetornarZeroParaUsuarioSemGastos() {
        BigDecimal totalCalculado = gastoRepository.calcularTotal(99L);

        assertNotNull(totalCalculado);
        assertEquals(BigDecimal.ZERO, totalCalculado, "O total deve ser zero para um usuário sem gastos.");
    }

    // ----------------------------------------------------
    // TESTE: calcularTotalPeriodo
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve calcular o total de gastos dentro do período especificado para o usuário")
    void calcularTotalPeriodo_DeveCalcularCorretamente() {
        LocalDate inicio = LocalDate.of(2025, 4, 1);
        LocalDate fim = LocalDate.of(2025, 4, 30);

        BigDecimal totalEsperado = new BigDecimal("250.00");

        BigDecimal totalCalculado = gastoRepository.calcularTotalPeriodo(MOCK_USER_ID, inicio, fim);

        assertNotNull(totalCalculado);
        assertEquals(0, totalEsperado.compareTo(totalCalculado), "O total calculado no período deve ser 250.00");
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver gastos no período")
    void calcularTotalPeriodo_DeveRetornarZeroQuandoVazio() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);

        BigDecimal totalCalculado = gastoRepository.calcularTotalPeriodo(MOCK_USER_ID, inicio, fim);

        assertNotNull(totalCalculado);
        assertEquals(BigDecimal.ZERO, totalCalculado, "O total deve ser zero para um período sem gastos.");
    }

    @Test
    @DisplayName("Deve ignorar gastos de outros usuários no período")
    void calcularTotalPeriodo_DeveIgnorarOutrosUsuarios() {
        LocalDate inicio = LocalDate.of(2025, 4, 1);
        LocalDate fim = LocalDate.of(2025, 4, 30);

        BigDecimal totalCalculado = gastoRepository.calcularTotalPeriodo(OTHER_USER_ID, inicio, fim);

        assertNotNull(totalCalculado);
        assertEquals(0, new BigDecimal("1000.00").compareTo(totalCalculado), "O total calculado deve ser 1000.00 para o OTHER_USER_ID.");

        BigDecimal totalCalculadoMock = gastoRepository.calcularTotalPeriodo(MOCK_USER_ID, inicio, fim);
        assertEquals(0, new BigDecimal("250.00").compareTo(totalCalculadoMock), "O total calculado deve ser 250.00 para o MOCK_USER_ID.");
    }
}