package br.com.fintech.repository;

import br.com.fintech.model.ObjetivoFinanceiro;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjetivoFinanceiroRepository extends OwnedEntityRepository<ObjetivoFinanceiro, Long> {
    List<ObjetivoFinanceiro> findByUsuarioId(Long usuarioId);
}