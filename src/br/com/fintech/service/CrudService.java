package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.OwnedEntity;
import br.com.fintech.repository.OwnedEntityRepository;

import java.util.List;
import java.util.Optional;

public abstract class CrudService<T extends OwnedEntity, ID> {
    protected final OwnedEntityRepository<T, ID> repository;

    protected CrudService(OwnedEntityRepository<T, ID> repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public List<T> findAllByOwnerId(Long ownerId) {
        return repository.findByUsuarioId(ownerId);
    }

    public T fetchOrThrowExceptionByOwner(ID id, Long ownerId) throws EntityNotFoundException {
        T entity = repository.findByIdAndUsuarioId(id, ownerId).orElseThrow(() ->
                new EntityNotFoundException("Recurso não encontrado com o ID: " + id + " para o usuário, ou acesso negado.")
        );

        if (entity.getUsuarioId() == null || !entity.getUsuarioId().equals(ownerId)) {
            throw new EntityNotFoundException("Acesso negado. Recurso não pertence ao usuário.");
        }

        return entity;
    }

    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    public void deleteByIdAndOwnerId(ID id, Long ownerId) throws EntityNotFoundException {
        T entity = fetchOrThrowExceptionByOwner(id, ownerId);
        repository.delete(entity);
    }
}