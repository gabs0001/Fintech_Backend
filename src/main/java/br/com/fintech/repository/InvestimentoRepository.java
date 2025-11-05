package br.com.fintech.repository;

import br.com.fintech.model.Investimento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvestimentoRepository extends OwnedEntityRepository<Investimento, Long> {
    @Query("SELECT COALESCE(SUM(i.valor), 0) FROM Investimento i WHERE i.usuarioId = :userId")
    BigDecimal calcularTotal(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(i.valor), 0) FROM Investimento i WHERE i.usuarioId = :userId AND i.dataRealizacao BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPeriodo(
            @Param("userId") Long userId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(
            value = "SELECT * FROM T_SIF_INVESTIMENTO i WHERE i.COD_USUARIO = ?1 ORDER BY i.DAT_REALIZACAO DESC FETCH FIRST ?2 ROWS ONLY",
            nativeQuery = true
    )
    List<Investimento> findUltimosInvestimentos(Long usuarioId, int limite);
}