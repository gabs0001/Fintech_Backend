package br.com.fintech.service;

import br.com.fintech.dao.CrudDAO;
import br.com.fintech.exceptions.EntityNotFoundException;

import java.sql.SQLException;
import java.util.List;

public abstract class CrudService<T, ID> {
    protected final CrudDAO<T, ID> dao;

    protected CrudService(CrudDAO<T, ID> dao) {
        this.dao = dao;
    }

    public List<T> getAllByUserId(Long ownerId) throws SQLException {
        return dao.getAllByUserId(ownerId);
    }

    public T getById(ID idEntity, Long ownerId) throws SQLException {
        return dao.getById(idEntity, ownerId);
    }

    protected T fetchOrThrowException(ID idEntity, Long ownerId) throws SQLException, EntityNotFoundException {
        T entity = dao.getById(idEntity, ownerId);

        if(entity == null) {
            throw new EntityNotFoundException("Entidade com ID: " + idEntity + " não encontrada para o usuário!");
        }

        return entity;
    }

    public T insert(T entity) throws SQLException {
        return dao.insert(entity);
    }

    public T update(Long ownerId, T entity) throws SQLException, EntityNotFoundException {
        return dao.update(ownerId, entity);
    }

    public void remove(ID idEntity, Long ownerId) throws SQLException, EntityNotFoundException {
        dao.remove(idEntity, ownerId);
    }
}