package br.com.fintech.repository;

import br.com.fintech.model.TipoRecebimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRecebimentoRepository extends JpaRepository<TipoRecebimento, Long> {}
