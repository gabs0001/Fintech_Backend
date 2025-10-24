package br.com.fintech.service;

import br.com.fintech.dao.CategoriaBaseDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public abstract class CategoriaBaseService<T, D extends CategoriaBaseDAO> {
    private final D dao;

    public CategoriaBaseService(D dao) {
        this.dao = dao;
    }

    protected abstract void validar(T entidade) throws IllegalArgumentException;

    protected abstract T fetchOrThrowException(Long categoriaId) throws SQLException, EntityNotFoundException;

    public List<T> getAll() throws SQLException {
        return (List<T>) dao.getAll();
    }

    public T getById(Long entityId) throws SQLException {
        Object result = dao.getById(entityId);
        if(result == null) {
            throw new EntityNotFoundException("Erro: Entidade de metadado (ID: " + entityId + ") não encontrada");
        }
        return (T) result;
    }

    public T insert(T novaCategoria) throws SQLException {
        validar(novaCategoria);
        return dao.insert(novaCategoria);
    }

    public T update(Long idEntity, T categoriaParaAlterar) throws SQLException, EntityNotFoundException {
        validar(categoriaParaAlterar);

        T existente = getById(idEntity);

        return dao.update(idEntity, categoriaParaAlterar);
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        dao.remove(id);
    }
}