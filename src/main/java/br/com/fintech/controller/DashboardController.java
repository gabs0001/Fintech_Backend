package br.com.fintech.controller;

import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final RelatorioService relatorioService;
    private static final Long MOCK_USER_ID = 1L;

    public DashboardController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> buscarTodos(
            @RequestParam(value = "limite", defaultValue = "5") int limite,
            @RequestParam("inicio") LocalDate inicio,
            @RequestParam("fim") LocalDate fim
    ) {
        Long userId = MOCK_USER_ID;

        DashboardDTO dashboardDTO =  relatorioService.getDashboard(userId, limite, inicio, fim);

        return ResponseEntity.ok(dashboardDTO);
    }

    @GetMapping("/saldo-geral")
    public ResponseEntity<BigDecimal> getSaldoGeral() {
        Long userId = MOCK_USER_ID;

        BigDecimal saldoGeral =  relatorioService.calcularSaldoGeral(userId);

        return ResponseEntity.ok(saldoGeral);
    }

    @GetMapping("/total-investido")
    public ResponseEntity<BigDecimal> getTotalInvestido() {
        Long userId = MOCK_USER_ID;

        BigDecimal totalInvestido =  relatorioService.calcularTotalInvestido(userId);

        return ResponseEntity.ok(totalInvestido);
    }

    @GetMapping("/ultimo-gasto")
    public ResponseEntity<Gasto> getUltimoGasto() {
        Long userId = MOCK_USER_ID;

        Optional<Gasto> optionalGasto = relatorioService.getUltimoGasto(userId);

        return optionalGasto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ultimo-recebimento")
    public ResponseEntity<Recebimento> getUltimoRecebimento() {
        Long userId = MOCK_USER_ID;

        Optional<Recebimento> optionalRecebimento = relatorioService.getUltimoRecebimento(userId);

        return optionalRecebimento
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/saldo-por-periodo")
    public ResponseEntity<BigDecimal> getSaldoPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {
        Long userId = MOCK_USER_ID;

        BigDecimal saldoPeriodo = relatorioService.calcularSaldoPeriodo(userId, inicio, fim);

        return ResponseEntity.ok(saldoPeriodo);
    }
}