package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.model.Gasto;
import br.com.fintech.repository.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional; // Importação necessária

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GastoServiceTest {
    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private CategoriaGastoService categoriaGastoService;

    @InjectMocks
    private GastoService gastoService;

    private static final Long MOCK_USER_ID = 1L;
    private static final Long GASTO_ID = 10L;
    private static final Long CATEGORIA_ID = 5L;
    private Gasto gastoValido;
    private CategoriaGasto mockCategoria;

    @BeforeEach
    void setUp() {
        mockCategoria = new CategoriaGasto();
        mockCategoria.setId(CATEGORIA_ID);

        gastoValido = new Gasto();
        gastoValido.setId(GASTO_ID);
        gastoValido.setUsuarioId(MOCK_USER_ID);
        gastoValido.setValor(new BigDecimal("100.00"));
        gastoValido.setDescricao("Compra de supermercado");
        gastoValido.setDataGasto(LocalDate.now());
        gastoValido.setCategoriaGasto(mockCategoria);
    }

    @Test
    @DisplayName("Deve inserir um novo Gasto válido com sucesso")
    void insert_GastoValido_DeveInserirComSucesso() {
        doReturn(mockCategoria).when(categoriaGastoService).getById(CATEGORIA_ID);
        when(gastoRepository.save(any(Gasto.class))).thenReturn(gastoValido);

        Gasto gastoSalvo = gastoService.insert(gastoValido);

        assertNotNull(gastoSalvo);

        verify(categoriaGastoService, times(1)).getById(CATEGORIA_ID);
        verify(gastoRepository, times(1)).save(gastoValido);
    }

    @Test
    @DisplayName("Não deve inserir Gasto se o valor for zero ou negativo (validarValor)")
    void insert_GastoComValorInvalido_DeveLancarException() {
        gastoValido.setValor(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> gastoService.insert(gastoValido));

        gastoValido.setValor(new BigDecimal("-50.00"));
        assertThrows(IllegalArgumentException.class, () -> gastoService.insert(gastoValido));

        verify(gastoRepository, never()).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Não deve inserir Gasto se a data for futura")
    void insert_GastoComDataFutura_DeveLancarException() {
        gastoValido.setDataGasto(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> gastoService.insert(gastoValido));

        verify(gastoRepository, never()).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Não deve inserir Gasto se a descrição for nula ou vazia")
    void insert_GastoComDescricaoInvalida_DeveLancarException() {
        gastoValido.setDescricao("");
        assertThrows(IllegalArgumentException.class, () -> gastoService.insert(gastoValido));

        gastoValido.setDescricao(null);
        assertThrows(IllegalArgumentException.class, () -> gastoService.insert(gastoValido));

        verify(gastoRepository, never()).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Não deve inserir Gasto se a Categoria for nula")
    void insert_GastoSemCategoria_DeveLancarException() {
        gastoValido.setCategoriaGasto(null);

        assertThrows(IllegalArgumentException.class, () -> gastoService.insert(gastoValido));

        verify(categoriaGastoService, never()).getById(any());
        verify(gastoRepository, never()).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Não deve inserir Gasto se a Categoria não existir (EntityNotFoundException)")
    void insert_GastoComCategoriaInexistente_DeveLancarEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Categoria não encontrada")).when(categoriaGastoService).getById(CATEGORIA_ID);

        assertThrows(EntityNotFoundException.class, () -> gastoService.insert(gastoValido));

        verify(gastoRepository, never()).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Deve atualizar um Gasto existente com sucesso")
    void update_GastoValido_DeveAtualizarComSucesso() {
        doReturn(Optional.of(gastoValido)).when(gastoRepository).findByIdAndUsuarioId(GASTO_ID, MOCK_USER_ID);
        when(gastoRepository.save(any(Gasto.class))).thenReturn(gastoValido);

        Gasto gastoAtualizado = gastoService.update(MOCK_USER_ID, gastoValido);

        assertNotNull(gastoAtualizado);

        verify(gastoRepository, times(1)).findByIdAndUsuarioId(GASTO_ID, MOCK_USER_ID);
        verify(gastoRepository, times(1)).save(gastoValido);
    }

    @Test
    @DisplayName("Não deve atualizar Gasto se o ID do objeto for nulo")
    void update_GastoSemId_DeveLancarException() {
        gastoValido.setId(null);

        assertThrows(IllegalArgumentException.class, () -> gastoService.update(MOCK_USER_ID, gastoValido));

        verify(gastoRepository, never()).save(any(Gasto.class));
    }

    @Test
    @DisplayName("Não deve atualizar Gasto se ele não pertencer ao usuário (Segurança)")
    void update_GastoNaoExistente_DeveLancarEntityNotFoundException() {
        doReturn(Optional.empty()).when(gastoRepository).findByIdAndUsuarioId(GASTO_ID, MOCK_USER_ID);

        assertThrows(EntityNotFoundException.class, () -> gastoService.update(MOCK_USER_ID, gastoValido));

        verify(gastoRepository, times(1)).findByIdAndUsuarioId(GASTO_ID, MOCK_USER_ID);
        verify(gastoRepository, never()).save(any(Gasto.class));
    }
}