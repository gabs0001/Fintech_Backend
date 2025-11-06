package br.com.fintech.controller;

import br.com.fintech.dto.DashboardDTO;
import br.com.fintech.dto.GastoDTO;
import br.com.fintech.dto.RecebimentoDTO;
import br.com.fintech.service.RelatorioService;
import br.com.fintech.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final RelatorioService relatorioService;
    private final JwtService jwtService;

    public DashboardController(RelatorioService relatorioService, JwtService jwtService) {
        this.relatorioService = relatorioService;
        this.jwtService = jwtService;
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtService.getUserIdFromToken(token);
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> buscarTodos(
            @RequestParam(value = "limite", defaultValue = "5") int limite,
            @RequestParam("inicio") LocalDate inicio,
            @RequestParam("fim") LocalDate fim,
            HttpServletRequest request
    ) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        DashboardDTO dashboardDTO = relatorioService.getDashboard(userId, limite, inicio, fim);
        return ResponseEntity.ok(dashboardDTO);
    }

    @GetMapping("/saldo-geral")
    public ResponseEntity<BigDecimal> getSaldoGeral(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BigDecimal saldoGeral = relatorioService.calcularSaldoGeral(userId);
        return ResponseEntity.ok(saldoGeral);
    }

    @GetMapping("/total-investido")
    public ResponseEntity<BigDecimal> getTotalInvestido(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BigDecimal totalInvestido = relatorioService.calcularTotalInvestido(userId);
        return ResponseEntity.ok(totalInvestido);
    }

    @GetMapping("/ultimo-gasto")
    public ResponseEntity<GastoDTO> getUltimoGasto(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<GastoDTO> optionalGastoDTO = relatorioService.getUltimoGasto(userId)
                .map(GastoDTO::new);

        return optionalGastoDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ultimo-recebimento")
    public ResponseEntity<RecebimentoDTO> getUltimoRecebimento(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<RecebimentoDTO> optionalRecebimentoDTO = relatorioService.getUltimoRecebimento(userId)
                .map(RecebimentoDTO::new);

        return optionalRecebimentoDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/saldo-por-periodo")
    public ResponseEntity<BigDecimal> getSaldoPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            HttpServletRequest request
    ) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BigDecimal saldoPeriodo = relatorioService.calcularSaldoPeriodo(userId, inicio, fim);
        return ResponseEntity.ok(saldoPeriodo);
    }
}