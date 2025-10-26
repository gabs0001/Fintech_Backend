package br.com.fintech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface OwnedEntityRepository<T, ID> extends JpaRepository<T, ID> {
    List<T> findByUsuarioId(Long usuarioId);
    Optional<T> findByIdAndUsuarioId(ID id, Long usuarioId);
}