package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public interface CrudDAO<T, ID> {
    T insert(T entity) throws SQLException;
    List<T> getAllByUserId(Long ownerId) throws SQLException;
    T getById(ID entityId, Long ownerId) throws SQLException;
    T update(Long ownerId, T entity) throws SQLException, EntityNotFoundException;
    void remove(ID idEntity, Long ownerId) throws SQLException, EntityNotFoundException;
}