package br.com.fintech.repository;

import br.com.fintech.model.Investimento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvestimentoRepository extends OwnedEntityRepository<Investimento, Long> {
    @Query("SELECT SUM(i.valor) FROM Investimento i WHERE i.usuarioId = :userId")
    BigDecimal calcularTotal(Long userId);

    @Query("SELECT SUM(i.valor) FROM Investimento i WHERE i.usuarioId = :userId AND i.dataRealizacao BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim);

    List<Investimento> findTopByUsuarioIdOrderByDataRealizacaoDesc(Long usuarioId);
    List<Investimento> findTopNByUsuarioIdOrderByDataRealizacaoDesc(Long usuarioId, int limite);
}