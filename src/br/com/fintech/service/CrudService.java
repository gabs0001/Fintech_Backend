package br.com.fintech.service;

import br.com.fintech.dao.CrudDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public abstract class CrudService<T, ID> {
    protected CrudDAO<T, ID> dao;

    protected CrudService(CrudDAO<T, ID> dao) {
        this.dao = dao;
    }

    public List<T> getAllByUserId(Long userId) throws SQLException {
        return dao.getAllByUserId(userId);
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

    public T insert(T entity) throws SQLException {
        return dao.insert(entity);
    }

    public T update(ID userId, T entity) throws SQLException, EntityNotFoundException {
        return dao.update(userId, entity);
    }

    public void remove(ID idEntity, ID idUser) throws SQLException, EntityNotFoundException {
        fetchOrThrowException(idEntity, idUser);
        dao.remove(idEntity);
    }
}