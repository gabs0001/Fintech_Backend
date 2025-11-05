package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Investimento;
import br.com.fintech.service.InvestimentoService;
import br.com.fintech.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investimentos")
public class InvestimentoController {
    private final InvestimentoService investimentoService;
    private final JwtService jwtService;

    public InvestimentoController(InvestimentoService investimentoService, JwtService jwtService) {
        this.investimentoService = investimentoService;
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
    public ResponseEntity<List<Investimento>> buscarTodos(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Investimento> todosOsInvestimentos = investimentoService.findAllByOwnerId(userId);
        return ResponseEntity.ok(todosOsInvestimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investimento> buscarPorId(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Investimento investimentoPorId = investimentoService.getById(id, userId);
        return ResponseEntity.ok(investimentoPorId);
    }

    @PostMapping
    public ResponseEntity<Investimento> salvar(@RequestBody Investimento investimento, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        investimento.setUsuarioId(userId);
        Investimento novoInvestimento = investimentoService.insert(investimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoInvestimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investimento> atualizar(@PathVariable("id") Long id, @RequestBody Investimento investimento, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        investimento.setId(id);
        investimento.setUsuarioId(userId);
        Investimento investimentoParaAtualizar = investimentoService.update(userId, investimento);
        return ResponseEntity.ok(investimentoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        investimentoService.remove(id, userId);
        return ResponseEntity.noContent().build();
    }
}