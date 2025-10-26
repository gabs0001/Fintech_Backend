package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class CategoriaBaseService<T, ID> {
    private final JpaRepository<T, ID> repository;

    public CategoriaBaseService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    protected abstract void validar(T entidade) throws IllegalArgumentException;

    public List<T> getAll() {
        return repository.findAll();
    }

    public T fetchOrThrowException(ID entityId) throws EntityNotFoundException {
        return repository.findById(entityId).orElseThrow(() ->
                new EntityNotFoundException("Erro: Entidade com (ID: " + entityId + ") não encontrada.")
        );
    }

    public T insert(T novaCategoria) throws IllegalArgumentException {
        validar(novaCategoria);
        return repository.save(novaCategoria);
    }

    public T update(ID idEntity, T categoriaParaAlterar) throws IllegalArgumentException, EntityNotFoundException {
        validar(categoriaParaAlterar);

        fetchOrThrowException(idEntity);

        return repository.save(categoriaParaAlterar);
    }

    public void remove(ID id) throws EntityNotFoundException {
        fetchOrThrowException(id);

        repository.deleteById(id);
    }
}