package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.ObjetivoFinanceiro;
import br.com.fintech.service.ObjetivoFinanceiroService;
import br.com.fintech.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objetivos-financeiros")
public class ObjetivoFinanceiroController {
    private final ObjetivoFinanceiroService objetivoFinanceiroService;
    private final JwtService jwtService;

    public ObjetivoFinanceiroController(ObjetivoFinanceiroService objetivoFinanceiroService, JwtService jwtService) {
        this.objetivoFinanceiroService = objetivoFinanceiroService;
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
    public ResponseEntity<List<ObjetivoFinanceiro>> buscarTodos(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<ObjetivoFinanceiro> todosOsObjetivosFinanceiros = objetivoFinanceiroService.findAllByOwnerId(userId);
        return ResponseEntity.ok(todosOsObjetivosFinanceiros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> buscarPorId(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        ObjetivoFinanceiro objetivoPorId = objetivoFinanceiroService.getById(id, userId);
        return ResponseEntity.ok(objetivoPorId);
    }

    @PostMapping
    public ResponseEntity<ObjetivoFinanceiro> salvar(@RequestBody ObjetivoFinanceiro objetivoFinanceiro, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        objetivoFinanceiro.setUsuarioId(userId);
        ObjetivoFinanceiro novoObjetivoFinanceiro = objetivoFinanceiroService.insert(objetivoFinanceiro);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoObjetivoFinanceiro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> atualizar(
            @PathVariable("id") Long id,
            @RequestBody ObjetivoFinanceiro objetivoFinanceiro,
            HttpServletRequest request
    ) throws IllegalArgumentException, EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        objetivoFinanceiro.setId(id);
        objetivoFinanceiro.setUsuarioId(userId);
        ObjetivoFinanceiro objetivoParaAtualizar = objetivoFinanceiroService.update(userId, objetivoFinanceiro);
        return ResponseEntity.ok(objetivoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        objetivoFinanceiroService.deleteByIdAndOwnerId(id, userId);
        return ResponseEntity.noContent().build();
    }
}