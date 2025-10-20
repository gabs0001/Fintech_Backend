package br.com.fintech.controller;

import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.model.Gasto;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.RelatorioService;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<DashboardDTO> buscarTodos(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "limite", defaultValue = "5") int limite,
            @RequestParam("inicio") LocalDate inicio,
            @RequestParam("fim") LocalDate fim
    ) {
        try {
            DashboardDTO dashboardDTO =  relatorioService.getDashboard(userId, limite, inicio, fim);
            return ResponseEntity.ok(dashboardDTO);
        }
        catch(SQLException e) {
            System.err.println("Erro no banco de dados durante a consulta do Dashboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/saldo-geral/{id}")
    public ResponseEntity<BigDecimal> getSaldoGeral(@PathVariable Long id) {
        try {
           BigDecimal saldoGeral =  relatorioService.calcularSaldoGeral(id);
           return ResponseEntity.ok(saldoGeral);
        }
        catch (SQLException e) {
            System.err.println("Erro ao tentar buscar o saldo geral: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total-investido/{id}")
    public ResponseEntity<BigDecimal> getTotalInvestido(@PathVariable Long id) {
        try {
            BigDecimal totalInvestido =  relatorioService.calcularTotalInvestido(id);
            return ResponseEntity.ok(totalInvestido);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar o total investido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ultimo-gasto/{id}")
    public ResponseEntity<Gasto> getUltimoGasto(@PathVariable Long id) {
        try {
            Gasto gasto = relatorioService.getUltimoGasto(id);

            if(gasto == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(gasto);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar o último gasto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ultimo-recebimento/{id}")
    public ResponseEntity<Recebimento> getUltimoRecebimento(@PathVariable Long id) {
        try {
            Recebimento recebimento = relatorioService.getUltimoRecebimento(id);

            if(recebimento == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(recebimento);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar o último recebimento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/saldo-por-periodo")
    public ResponseEntity<BigDecimal> getSaldoPorPeriodo(@RequestParam Long id, @RequestParam LocalDate inicio, @RequestParam LocalDate fim) {
        try {
            BigDecimal saldoPeriodo = relatorioService.calcularSaldoPeriodo(id, inicio, fim);
            return ResponseEntity.ok(saldoPeriodo);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar o saldo no período definido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}