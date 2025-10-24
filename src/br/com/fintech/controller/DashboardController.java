package br.com.fintech.controller;

import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final RelatorioService relatorioService;

    public DashboardController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    private Long getAuthenticatedUserId() { return 1L; }

    @GetMapping
    public ResponseEntity<DashboardDTO> buscarTodos(
            @RequestParam(value = "limite", defaultValue = "5") int limite,
            @RequestParam("inicio") LocalDate inicio,
            @RequestParam("fim") LocalDate fim
    ) throws SQLException
    {
        Long userId = getAuthenticatedUserId();

        DashboardDTO dashboardDTO =  relatorioService.getDashboard(userId, limite, inicio, fim);

        return ResponseEntity.ok(dashboardDTO);
    }

    @GetMapping("/saldo-geral")
    public ResponseEntity<BigDecimal> getSaldoGeral() throws SQLException {
        Long userId = getAuthenticatedUserId();

        BigDecimal saldoGeral =  relatorioService.calcularSaldoGeral(userId);

        return ResponseEntity.ok(saldoGeral);
    }

    @GetMapping("/total-investido")
    public ResponseEntity<BigDecimal> getTotalInvestido() throws SQLException {
        Long userId = getAuthenticatedUserId();

        BigDecimal totalInvestido =  relatorioService.calcularTotalInvestido(userId);

        return ResponseEntity.ok(totalInvestido);
    }

    @GetMapping("/ultimo-gasto")
    public ResponseEntity<Gasto> getUltimoGasto() throws SQLException {
        Long userId = getAuthenticatedUserId();

        Gasto gasto = relatorioService.getUltimoGasto(userId);

        if(gasto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(gasto);
    }

    @GetMapping("/ultimo-recebimento")
    public ResponseEntity<Recebimento> getUltimoRecebimento() throws SQLException {
        Long userId = getAuthenticatedUserId();

        Recebimento recebimento = relatorioService.getUltimoRecebimento(userId);

        if(recebimento == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(recebimento);
    }

    @GetMapping("/saldo-por-periodo")
    public ResponseEntity<BigDecimal> getSaldoPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) throws SQLException
    {
        Long userId = getAuthenticatedUserId();

        BigDecimal saldoPeriodo = relatorioService.calcularSaldoPeriodo(userId, inicio, fim);

        return ResponseEntity.ok(saldoPeriodo);
    }
}