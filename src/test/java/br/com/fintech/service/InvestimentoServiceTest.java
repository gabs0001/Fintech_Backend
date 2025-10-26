package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Instituicao;
import br.com.fintech.model.Investimento;
import br.com.fintech.model.TipoInvestimento;
import br.com.fintech.repository.InvestimentoRepository;
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
class InvestimentoServiceTest {
    @Mock
    private InvestimentoRepository investimentoRepository;

    @Mock
    private TipoInvestimentoService tipoInvestimentoService;

    @Mock
    private InstituicaoService instituicaoService;

    @InjectMocks
    private InvestimentoService investimentoService;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long INVESTIMENTO_ID = 30L;
    private static final Long TIPO_ID = 7L;
    private static final Long INSTITUICAO_ID = 12L;
    private Investimento investimentoValido;
    private TipoInvestimento mockTipo;
    private Instituicao mockInstituicao;

    @BeforeEach
    void setUp() throws EntityNotFoundException {
        mockTipo = new TipoInvestimento();
        mockTipo.setId(TIPO_ID);
        mockInstituicao = new Instituicao();
        mockInstituicao.setId(INSTITUICAO_ID);

        investimentoValido = new Investimento();
        investimentoValido.setId(INVESTIMENTO_ID);
        investimentoValido.setUsuarioId(MOCK_USER_ID);
        investimentoValido.setNome("CDB Banco X");
        investimentoValido.setDescricao("Renda Fixa de alta liquidez");
        investimentoValido.setValor(new BigDecimal("1000.00"));
        investimentoValido.setDataRealizacao(LocalDate.now());
        investimentoValido.setDataVencimento(LocalDate.now().plusYears(2));

        investimentoValido.setTipoInvestimento(mockTipo);
        investimentoValido.setInstituicao(mockInstituicao);

        doReturn(mockTipo).when(tipoInvestimentoService).getById(TIPO_ID);
        doReturn(mockInstituicao).when(instituicaoService).fetchOrThrowException(INSTITUICAO_ID);
    }

    @Test
    @DisplayName("Deve inserir um novo Investimento válido com sucesso")
    void insert_InvestimentoValido_DeveInserirComSucesso() throws Exception {
        when(investimentoRepository.save(any(Investimento.class))).thenReturn(investimentoValido);

        Investimento investimentoSalvo = investimentoService.insert(investimentoValido);

        assertNotNull(investimentoSalvo);

        verify(tipoInvestimentoService, times(1)).getById(TIPO_ID);
        verify(instituicaoService, times(1)).fetchOrThrowException(INSTITUICAO_ID);
        verify(investimentoRepository, times(1)).save(investimentoValido);
    }

    @Test
    @DisplayName("Não deve inserir Investimento se o valor for zero ou negativo")
    void insert_InvestimentoComValorInvalido_DeveLancarException() {
        investimentoValido.setValor(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        investimentoValido.setValor(new BigDecimal("-1.00"));
        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        verify(investimentoRepository, never()).save(any(Investimento.class));
    }

    @Test
    @DisplayName("Não deve inserir Investimento se a data de realização for futura")
    void insert_InvestimentoComDataRealizacaoFutura_DeveLancarException() {
        investimentoValido.setDataRealizacao(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        verify(investimentoRepository, never()).save(any(Investimento.class));
    }

    @Test
    @DisplayName("Não deve inserir Investimento se a data de vencimento for passada/hoje")
    void insert_InvestimentoComDataVencimentoPassada_DeveLancarException() {
        investimentoValido.setDataVencimento(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        investimentoValido.setDataVencimento(null);
        assertDoesNotThrow(() -> investimentoService.insert(investimentoValido));
    }

    @Test
    @DisplayName("Não deve inserir Investimento se descrição ou nome forem nulos/vazios")
    void insert_InvestimentoSemDescricaoOuNome_DeveLancarException() {
        investimentoValido.setDescricao("");
        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        investimentoValido.setDescricao("Valida");
        investimentoValido.setNome(null);
        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        verify(investimentoRepository, never()).save(any(Investimento.class));
    }

    @Test
    @DisplayName("Não deve inserir se o TipoInvestimento (objeto) for nulo")
    void insert_InvestimentoSemTipoObjetoValido_DeveLancarException() {
        investimentoValido.setTipoInvestimento(null);

        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        verify(tipoInvestimentoService, never()).getById(any());
        verify(instituicaoService, never()).fetchOrThrowException(any());
    }

    @Test
    @DisplayName("Não deve inserir se a Instituição (objeto) for nula")
    void insert_InvestimentoSemInstituicaoObjetoValida_DeveLancarException() {
        investimentoValido.setInstituicao(null);

        assertThrows(IllegalArgumentException.class, () -> investimentoService.insert(investimentoValido));

        verify(tipoInvestimentoService, times(1)).getById(any());
        verify(instituicaoService, never()).fetchOrThrowException(any());
    }

    @Test
    @DisplayName("Não deve inserir se TipoInvestimento for inválido (ID inexistente)")
    void insert_InvestimentoComTipoInexistente_DeveLancarEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Tipo não encontrado")).when(tipoInvestimentoService).getById(TIPO_ID);

        assertThrows(EntityNotFoundException.class, () -> investimentoService.insert(investimentoValido));
    }

    @Test
    @DisplayName("Não deve inserir se Instituicao for inválida (ID inexistente)")
    void insert_InvestimentoComInstituicaoInexistente_DeveLancarEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Instituição não encontrada")).when(instituicaoService).fetchOrThrowException(INSTITUICAO_ID);

        assertThrows(EntityNotFoundException.class, () -> investimentoService.insert(investimentoValido));
    }

    @Test
    @DisplayName("Deve atualizar um Investimento existente com sucesso")
    void update_InvestimentoValido_DeveAtualizarComSucesso() throws Exception {
        when(investimentoRepository.save(any(Investimento.class))).thenReturn(investimentoValido);

        Investimento investimentoAtualizado = investimentoService.update(MOCK_USER_ID, investimentoValido);

        assertNotNull(investimentoAtualizado);
        verify(investimentoRepository, times(1)).save(investimentoValido);
    }

    @Test
    @DisplayName("Não deve atualizar Investimento se o ID do objeto for nulo")
    void update_InvestimentoSemId_DeveLancarException() {
        investimentoValido.setId(null);

        assertThrows(IllegalArgumentException.class, () -> investimentoService.update(MOCK_USER_ID, investimentoValido));

        verify(investimentoRepository, never()).save(any(Investimento.class));
    }

    @Test
    @DisplayName("Não deve atualizar Investimento se ele não pertencer ao usuário (Segurança)")
    void update_InvestimentoNaoExistente_DeveLancarEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Investimento inacessível")).when(investimentoService).fetchOrThrowExceptionByOwner(INVESTIMENTO_ID, MOCK_USER_ID);

        assertThrows(EntityNotFoundException.class, () -> investimentoService.update(MOCK_USER_ID, investimentoValido));

        verify(investimentoRepository, never()).save(any(Investimento.class));
    }
}