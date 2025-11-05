package br.com.fintech.repository;

import br.com.fintech.model.Gasto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends OwnedEntityRepository<Gasto, Long> {
    @Query("SELECT COALESCE(SUM(g.valor), 0) FROM Gasto g WHERE g.usuarioId = :userId")
    BigDecimal calcularTotal(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(g.valor), 0) FROM Gasto g WHERE g.usuarioId = :userId AND g.dataGasto BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPeriodo(
            @Param("userId") Long userId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query(
            value = "SELECT * FROM T_SIF_GASTO g WHERE g.COD_USUARIO = ?1 ORDER BY g.DAT_GASTO DESC FETCH FIRST ?2 ROWS ONLY",
            nativeQuery = true
    )
    List<Gasto> findUltimosGastos(Long usuarioId, int limite);
}