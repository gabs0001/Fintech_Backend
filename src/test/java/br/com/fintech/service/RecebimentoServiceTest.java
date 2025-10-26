package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;
import br.com.fintech.model.TipoRecebimento;
import br.com.fintech.repository.RecebimentoRepository;
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
class RecebimentoServiceTest {
    @Mock
    private RecebimentoRepository recebimentoRepository;

    @Mock
    private TipoRecebimentoService tipoRecebimentoService;

    @InjectMocks
    private RecebimentoService recebimentoService;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long RECEBIMENTO_ID = 20L;
    private static final Long TIPO_RECEBIMENTO_ID = 6L;
    private Recebimento recebimentoValido;
    private TipoRecebimento mockTipoRecebimento;

    @BeforeEach
    void setUp() throws EntityNotFoundException {
        mockTipoRecebimento = new TipoRecebimento();
        mockTipoRecebimento.setId(TIPO_RECEBIMENTO_ID);

        recebimentoValido = new Recebimento();
        recebimentoValido.setId(RECEBIMENTO_ID);
        recebimentoValido.setUsuarioId(MOCK_USER_ID);
        recebimentoValido.setValor(new BigDecimal("5000.00"));
        recebimentoValido.setDescricao("Salário Mensal");
        recebimentoValido.setDataRecebimento(LocalDate.now());
        recebimentoValido.setTipoRecebimento(mockTipoRecebimento);

        doReturn(mockTipoRecebimento).when(tipoRecebimentoService).getById(TIPO_RECEBIMENTO_ID);
    }

    // ----------------------------------------------------
    // TESTES DE INSERÇÃO (insert)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve inserir um novo Recebimento válido com sucesso")
    void insert_RecebimentoValido_DeveInserirComSucesso() throws Exception {
        when(recebimentoRepository.save(any(Recebimento.class))).thenReturn(recebimentoValido);

        Recebimento recebimentoSalvo = recebimentoService.insert(recebimentoValido);

        assertNotNull(recebimentoSalvo);

        verify(tipoRecebimentoService, times(1)).getById(TIPO_RECEBIMENTO_ID);
        verify(recebimentoRepository, times(1)).save(recebimentoValido);
    }

    // ----------------------------------------------------
    // TESTES DE VALIDAÇÃO (insert e update)
    // ----------------------------------------------------

    @Test
    @DisplayName("Não deve inserir Recebimento se o valor for zero ou negativo")
    void insert_RecebimentoComValorInvalido_DeveLancarException() {
        recebimentoValido.setValor(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> recebimentoService.insert(recebimentoValido));

        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("Não deve inserir Recebimento se a data for futura")
    void insert_RecebimentoComDataFutura_DeveLancarException() {
        recebimentoValido.setDataRecebimento(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> recebimentoService.insert(recebimentoValido));

        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("Não deve inserir Recebimento se a descrição for nula ou vazia")
    void insert_RecebimentoComDescricaoInvalida_DeveLancarException() {
        recebimentoValido.setDescricao("");
        assertThrows(IllegalArgumentException.class, () -> recebimentoService.insert(recebimentoValido));

        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("Não deve inserir Recebimento se o TipoRecebimento (objeto) for nulo")
    void insert_RecebimentoSemTipoRecebimento_DeveLancarException() {
        recebimentoValido.setTipoRecebimento(null);

        assertThrows(IllegalArgumentException.class, () -> recebimentoService.insert(recebimentoValido));

        verify(tipoRecebimentoService, never()).getById(any());
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("Não deve inserir Recebimento se o TipoRecebimentoId for nulo (mesmo que o objeto seja válido)")
    void insert_RecebimentoComTipoRecebimentoSemId_DeveLancarException() throws EntityNotFoundException {
        TipoRecebimento tipoSemId = new TipoRecebimento();
        recebimentoValido.setTipoRecebimento(tipoSemId);

        assertThrows(IllegalArgumentException.class, () -> recebimentoService.insert(recebimentoValido));

        verify(tipoRecebimentoService, never()).getById(any());
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }


    @Test
    @DisplayName("Não deve inserir Recebimento se o TipoRecebimento não existir (EntityNotFoundException)")
    void insert_RecebimentoComTipoRecebimentoInexistente_DeveLancarEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Tipo Recebimento não encontrado")).when(tipoRecebimentoService).getById(TIPO_RECEBIMENTO_ID);

        assertThrows(EntityNotFoundException.class, () -> recebimentoService.insert(recebimentoValido));

        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    // ----------------------------------------------------
    // TESTES DE ATUALIZAÇÃO (update)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve atualizar um Recebimento existente com sucesso")
    void update_RecebimentoValido_DeveAtualizarComSucesso() throws Exception {
        when(recebimentoRepository.save(any(Recebimento.class))).thenReturn(recebimentoValido);

        Recebimento recebimentoAtualizado = recebimentoService.update(MOCK_USER_ID, recebimentoValido);

        assertNotNull(recebimentoAtualizado);

        verify(recebimentoRepository, times(1)).save(recebimentoValido);
    }

    @Test
    @DisplayName("Não deve atualizar Recebimento se o ID do objeto for nulo")
    void update_RecebimentoSemId_DeveLancarException() {
        recebimentoValido.setId(null);

        assertThrows(IllegalArgumentException.class, () -> recebimentoService.update(MOCK_USER_ID, recebimentoValido));

        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("Não deve atualizar Recebimento se ele não pertencer ao usuário (Segurança)")
    void update_RecebimentoNaoExistente_DeveLancarEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Recebimento inacessível")).when(recebimentoService).fetchOrThrowExceptionByOwner(RECEBIMENTO_ID, MOCK_USER_ID);

        assertThrows(EntityNotFoundException.class, () -> recebimentoService.update(MOCK_USER_ID, recebimentoValido));

        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }
}