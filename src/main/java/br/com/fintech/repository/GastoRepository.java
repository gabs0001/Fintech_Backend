package br.com.fintech.repository;

import br.com.fintech.model.Gasto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoRepository extends OwnedEntityRepository<Gasto, Long> {
    @Query("SELECT SUM (g.valor) FROM Gasto g WHERE g.usuarioId = :userId")
    BigDecimal calcularTotal(Long userId);

    @Query("SELECT SUM(g.valor) FROM Gasto g WHERE g.usuarioId = :userId AND g.dataGasto BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim);

    List<Gasto> findTopByUsuarioIdOrderByDataGastoDesc(Long usuarioId);
    List<Gasto> findTopNByUsuarioIdOrderByDataGastoDesc(Long usuarioId, int limite);
}