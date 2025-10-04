package br.com.fintech.service;

import br.com.fintech.dao.CrudDAO;
import br.com.fintech.exceptions.EntityNotFoundException;

import java.sql.SQLException;
import java.util.List;

public abstract class CrudService<T, ID> {
    protected CrudDAO<T, ID> dao;

    protected CrudService(CrudDAO<T, ID> dao) {
        this.dao = dao;
    }

    public List<T> getAll() throws SQLException {
        return dao.getAll();
    }

    public T getById(ID idEntity, ID idUser) throws SQLException {
        return dao.getById(idEntity, idUser);
    }

    protected T fetchOrThrowException(ID idEntity, ID idUser) throws SQLException, EntityNotFoundException {
        T entity = dao.getById(idEntity, idUser);

        if(entity == null) {
            throw new EntityNotFoundException("Entidade com ID: " + idEntity + "não encontrada!");
        }

        return entity;
    }

    public void insert(T entity) throws SQLException {
        dao.insert(entity);
    }

    public void update(T entity) throws SQLException, EntityNotFoundException {
        dao.update(entity);
    }

    public void remove(ID idEntity, ID idUser) throws SQLException, EntityNotFoundException {
        fetchOrThrowException(idEntity, idUser);
        dao.remove(idEntity);
    }
}