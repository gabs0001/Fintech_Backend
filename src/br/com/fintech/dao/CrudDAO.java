package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO<T, ID> {
    void insert(T entity) throws SQLException;
    List<T> getAll() throws SQLException;
    T getById(ID entityId, ID userId) throws SQLException;
    void update(T entity) throws SQLException, EntityNotFoundException;
    void remove(ID id) throws SQLException, EntityNotFoundException;
}