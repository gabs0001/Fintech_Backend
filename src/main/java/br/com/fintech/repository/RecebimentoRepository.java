package br.com.fintech.repository;

import br.com.fintech.model.Recebimento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RecebimentoRepository extends OwnedEntityRepository<Recebimento, Long> {
    @Query("SELECT COALESCE(SUM(r.valor), 0) FROM Recebimento r WHERE r.usuarioId = :userId")
    BigDecimal calcularTotal(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(r.valor), 0) FROM Recebimento r WHERE r.usuarioId = :userId AND r.dataRecebimento BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPeriodo(
            @Param("userId") Long userId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(
            value = "SELECT * FROM T_SIF_RECEBIMENTO r WHERE r.COD_USUARIO = ?1 ORDER BY r.DAT_RECEBIMENTO DESC LIMIT ?2",
            nativeQuery = true
    )
    List<Recebimento> findUltimosRecebimentos(Long usuarioId, int limite);
}