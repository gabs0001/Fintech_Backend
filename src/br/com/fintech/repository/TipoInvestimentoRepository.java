package br.com.fintech.repository;

import br.com.fintech.model.TipoInvestimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoInvestimentoRepository extends JpaRepository<TipoInvestimento, Long> {}
