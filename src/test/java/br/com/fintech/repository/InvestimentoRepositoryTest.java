package br.com.fintech.repository;

import br.com.fintech.model.Instituicao;
import br.com.fintech.model.Investimento;
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
class InvestimentoRepositoryTest {
    @Autowired
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        Instituicao instituicao = new Instituicao();
        instituicao.setNome("TesteBank");
        entityManager.persistAndFlush(instituicao);

        Investimento inv1 = new Investimento();
        inv1.setUsuarioId(MOCK_USER_ID);
        inv1.setNome("Tesouro Selic");
        inv1.setValor(new BigDecimal("5000.00"));
        inv1.setDataRealizacao(LocalDate.of(2023, 1, 15));
        inv1.setInstituicao(instituicao);
        entityManager.persistAndFlush(inv1);

        Investimento inv2 = new Investimento();
        inv2.setUsuarioId(MOCK_USER_ID);
        inv2.setNome("CDB XPTO");
        inv2.setValor(new BigDecimal("10000.00"));
        inv2.setDataRealizacao(LocalDate.of(2024, 6, 1));
        inv2.setInstituicao(instituicao);
        entityManager.persistAndFlush(inv2);

        Investimento inv3 = new Investimento();
        inv3.setUsuarioId(MOCK_USER_ID);
        inv3.setNome("Fundo Imobiliário");
        inv3.setValor(new BigDecimal("2000.00"));
        inv3.setDataRealizacao(LocalDate.of(2025, 1, 1));
        inv3.setInstituicao(instituicao);
        entityManager.persistAndFlush(inv3);

        Investimento invOutroUser = new Investimento();
        invOutroUser.setUsuarioId(OTHER_USER_ID);
        invOutroUser.setValor(new BigDecimal("50000.00"));
        invOutroUser.setDataRealizacao(LocalDate.of(2024, 1, 1));
        invOutroUser.setInstituicao(instituicao);
        entityManager.persistAndFlush(invOutroUser);
    }

    // ----------------------------------------------------
    // TESTE: calcularTotal
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve calcular o total de todos os investimentos para um usuário específico")
    void calcularTotal_DeveRetornarTotalCorretoParaUsuario() {
        BigDecimal totalEsperado = new BigDecimal("17000.00");

        BigDecimal totalCalculado = investimentoRepository.calcularTotal(MOCK_USER_ID);

        assertNotNull(totalCalculado);
        assertEquals(0, totalEsperado.compareTo(totalCalculado));
    }

    @Test
    @DisplayName("Deve retornar zero se o usuário não tiver investimentos")
    void calcularTotal_DeveRetornarZeroParaUsuarioSemInvestimentos() {
        BigDecimal totalCalculado = investimentoRepository.calcularTotal(99L);

        assertNotNull(totalCalculado);
        assertEquals(BigDecimal.ZERO, totalCalculado);
    }

    @Test
    @DisplayName("Deve retornar o total correto para um usuário diferente")
    void calcularTotal_DeveRetornarTotalCorretoParaOutroUsuario() {
        BigDecimal totalEsperado = new BigDecimal("50000.00");

        BigDecimal totalCalculado = investimentoRepository.calcularTotal(OTHER_USER_ID);

        assertNotNull(totalCalculado);
        assertEquals(0, totalEsperado.compareTo(totalCalculado));
    }
}