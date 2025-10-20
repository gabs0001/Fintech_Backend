package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public interface CrudDAO<T, ID> {
    T insert(T entity) throws SQLException;
    List<T> getAllByUserId(Long userId) throws SQLException;
    T getById(ID entityId, ID userId) throws SQLException;
    T update(ID userId, T entity) throws SQLException, EntityNotFoundException;
    void remove(ID id) throws SQLException, EntityNotFoundException;
}