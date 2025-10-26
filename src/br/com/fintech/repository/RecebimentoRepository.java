package br.com.fintech.repository;

import br.com.fintech.model.Recebimento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecebimentoRepository extends OwnedEntityRepository<Recebimento, Long> {
    @Query("SELECT SUM(r.valor) FROM Recebimento r WHERE r.usuarioId = :userId")
    BigDecimal calcularTotal(Long userId);

    @Query("SELECT SUM(r.valor) FROM Recebimento r WHERE r.usuarioId = :userId AND r.dataRecebimento BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim);

    List<Recebimento> findTopByUsuarioIdOrderByDataRecebimentoDesc(Long usuarioId);
    List<Recebimento> findTopNByUsuarioIdOrderByDataRecebimentoDesc(Long usuarioId, int limite);
}