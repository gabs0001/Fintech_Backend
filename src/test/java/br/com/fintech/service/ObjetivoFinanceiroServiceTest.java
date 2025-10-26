package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.ObjetivoFinanceiro;
import br.com.fintech.repository.ObjetivoFinanceiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjetivoFinanceiroServiceTest {
    @Mock
    private ObjetivoFinanceiroRepository objetivoFinanceiroRepository;

    @InjectMocks
    private ObjetivoFinanceiroService objetivoFinanceiroService;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long OBJETIVO_ID = 40L;
    private ObjetivoFinanceiro objetivoValido;

    @BeforeEach
    void setUp() {
        objetivoValido = new ObjetivoFinanceiro();
        objetivoValido.setId(OBJETIVO_ID);
        objetivoValido.setUsuarioId(MOCK_USER_ID);
        objetivoValido.setDescricao("Comprar um apartamento");
        objetivoValido.setValor(new BigDecimal("50000.00"));
        objetivoValido.setDataConclusao(LocalDate.now().plusYears(5));
    }

    // ----------------------------------------------------
    // TESTES DE INSERÇÃO (insert)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve inserir um novo Objetivo Financeiro válido com sucesso")
    void insert_ObjetivoValido_DeveInserirComSucesso() throws Exception {
        when(objetivoFinanceiroRepository.save(any(ObjetivoFinanceiro.class))).thenReturn(objetivoValido);

        ObjetivoFinanceiro objetivoSalvo = objetivoFinanceiroService.insert(objetivoValido);

        assertNotNull(objetivoSalvo);
        verify(objetivoFinanceiroRepository, times(1)).save(objetivoValido);
    }

    // ----------------------------------------------------
    // TESTES DE VALIDAÇÃO (insert e update)
    // ----------------------------------------------------

    @Test
    @DisplayName("Não deve inserir Objetivo se o valor for zero ou negativo")
    void insert_ObjetivoComValorInvalido_DeveLancarException() {
        objetivoValido.setValor(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.insert(objetivoValido));

        objetivoValido.setValor(new BigDecimal("-100.00"));
        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.insert(objetivoValido));

        verify(objetivoFinanceiroRepository, never()).save(any(ObjetivoFinanceiro.class));
    }

    @Test
    @DisplayName("Não deve inserir Objetivo se a data de conclusão for hoje ou passada")
    void insert_ObjetivoComDataConclusaoInvalida_DeveLancarException() {
        objetivoValido.setDataConclusao(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.insert(objetivoValido));

        objetivoValido.setDataConclusao(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.insert(objetivoValido));

        objetivoValido.setDataConclusao(null);
        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.insert(objetivoValido));

        verify(objetivoFinanceiroRepository, never()).save(any(ObjetivoFinanceiro.class));
    }

    @Test
    @DisplayName("Não deve inserir Objetivo se a descrição for nula ou vazia")
    void insert_ObjetivoSemDescricao_DeveLancarException() {
        objetivoValido.setDescricao("  ");
        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.insert(objetivoValido));

        objetivoValido.setDescricao(null);
        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.insert(objetivoValido));

        verify(objetivoFinanceiroRepository, never()).save(any(ObjetivoFinanceiro.class));
    }

    // ----------------------------------------------------
    // TESTES DE ATUALIZAÇÃO (update)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve atualizar um Objetivo existente com sucesso")
    void update_ObjetivoValido_DeveAtualizarComSucesso() throws Exception {
        when(objetivoFinanceiroRepository.save(any(ObjetivoFinanceiro.class))).thenReturn(objetivoValido);

        ObjetivoFinanceiro objetivoAtualizado = objetivoFinanceiroService.update(MOCK_USER_ID, objetivoValido);

        assertNotNull(objetivoAtualizado);
        verify(objetivoFinanceiroRepository, times(1)).save(objetivoValido);
    }

    @Test
    @DisplayName("Não deve atualizar Objetivo se o ID do objeto for nulo")
    void update_ObjetivoSemId_DeveLancarException() {
        objetivoValido.setId(null);

        assertThrows(IllegalArgumentException.class, () -> objetivoFinanceiroService.update(MOCK_USER_ID, objetivoValido));

        verify(objetivoFinanceiroRepository, never()).save(any(ObjetivoFinanceiro.class));
    }

    @Test
    @DisplayName("Não deve atualizar Objetivo se ele não pertencer ao usuário (Segurança)")
    void update_ObjetivoNaoExistente_DeveLancarEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Objetivo inacessível")).when(objetivoFinanceiroService).fetchOrThrowExceptionByOwner(OBJETIVO_ID, MOCK_USER_ID);

        assertThrows(EntityNotFoundException.class, () -> objetivoFinanceiroService.update(MOCK_USER_ID, objetivoValido));

        verify(objetivoFinanceiroRepository, never()).save(any(ObjetivoFinanceiro.class));
    }
}