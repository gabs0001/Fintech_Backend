package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MockCategoria {
    private Long id;
    private String nome;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}

class MockCategoriaBaseService extends CategoriaBaseService<MockCategoria, Long> {
    public MockCategoriaBaseService(JpaRepository<MockCategoria, Long> repository) {
        super(repository);
    }

    @Override
    protected void validar(MockCategoria entidade) throws IllegalArgumentException {
        if (entidade.getNome() == null || entidade.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
    }
}

@ExtendWith(MockitoExtension.class)
class CategoriaBaseServiceTest {
    @Mock
    private JpaRepository<MockCategoria, Long> repository;

    @InjectMocks
    private MockCategoriaBaseService service;

    private static final Long MOCK_ID = 5L;
    private MockCategoria categoriaValida;

    @BeforeEach
    void setUp() {
        categoriaValida = new MockCategoria();
        categoriaValida.setId(MOCK_ID);
        categoriaValida.setNome("Teste Geral");
    }

    // ----------------------------------------------------
    // TESTES DE BUSCA (getById)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve buscar entidade por ID e retornar com sucesso")
    void getById_DeveRetornarEntidadeExistente() throws EntityNotFoundException {
        when(repository.findById(MOCK_ID)).thenReturn(Optional.of(categoriaValida));

        MockCategoria resultado = service.getById(MOCK_ID);

        assertNotNull(resultado);
        assertEquals(MOCK_ID, resultado.getId());
        verify(repository, times(1)).findById(MOCK_ID);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se entidade não for encontrada")
    void getById_DeveLancarEntityNotFoundException() {
        when(repository.findById(MOCK_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(MOCK_ID));
        verify(repository, times(1)).findById(MOCK_ID);
    }

    @Test
    @DisplayName("Deve retornar todas as entidades")
    void getAll_DeveRetornarListaDeEntidades() {
        List<MockCategoria> lista = List.of(categoriaValida, new MockCategoria());
        when(repository.findAll()).thenReturn(lista);

        List<MockCategoria> resultado = service.getAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    // ----------------------------------------------------
    // TESTES DE INSERÇÃO (insert)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve inserir entidade válida com sucesso")
    void insert_EntidadeValida_DeveSalvar() {
        when(repository.save(any(MockCategoria.class))).thenReturn(categoriaValida);

        MockCategoria resultado = service.insert(categoriaValida);

        assertNotNull(resultado);
        verify(repository, times(1)).save(categoriaValida);
    }

    @Test
    @DisplayName("Não deve inserir se a validação falhar (teste do método abstrato)")
    void insert_EntidadeInvalida_DeveLancarException() {
        categoriaValida.setNome("");

        assertThrows(IllegalArgumentException.class, () -> service.insert(categoriaValida));

        verify(repository, never()).save(any());
    }

    // ----------------------------------------------------
    // TESTES DE ATUALIZAÇÃO (update)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve atualizar entidade existente com sucesso")
    void update_EntidadeExistente_DeveAtualizar() throws EntityNotFoundException {
        when(repository.findById(MOCK_ID)).thenReturn(Optional.of(categoriaValida));
        when(repository.save(any(MockCategoria.class))).thenReturn(categoriaValida);

        MockCategoria resultado = service.update(MOCK_ID, categoriaValida);

        assertNotNull(resultado);
        verify(repository, times(1)).findById(MOCK_ID); // Verifica se a checagem de existência foi feita
        verify(repository, times(1)).save(categoriaValida);
    }

    @Test
    @DisplayName("Não deve atualizar se a entidade não existir (404)")
    void update_EntidadeInexistente_DeveLancarEntityNotFoundException() {
        when(repository.findById(MOCK_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(MOCK_ID, categoriaValida));

        verify(repository, times(1)).findById(MOCK_ID);
        verify(repository, never()).save(any());
    }

    // ----------------------------------------------------
    // TESTES DE REMOÇÃO (remove)
    // ----------------------------------------------------

    @Test
    @DisplayName("Deve remover entidade existente com sucesso")
    void remove_EntidadeExistente_DeveRemover() throws EntityNotFoundException {
        when(repository.findById(MOCK_ID)).thenReturn(Optional.of(categoriaValida));

        service.remove(MOCK_ID);

        verify(repository, times(1)).findById(MOCK_ID); // Verifica se a checagem de existência foi feita
        verify(repository, times(1)).deleteById(MOCK_ID);
    }

    @Test
    @DisplayName("Não deve remover se a entidade não existir (404)")
    void remove_EntidadeInexistente_DeveLancarEntityNotFoundException() {
        when(repository.findById(MOCK_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.remove(MOCK_ID));

        verify(repository, times(1)).findById(MOCK_ID);
        verify(repository, never()).deleteById(any());
    }
}