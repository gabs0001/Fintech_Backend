package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.RecebimentoService;
import br.com.fintech.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recebimentos")
public class RecebimentoController {
    private final RecebimentoService recebimentoService;
    private final JwtService jwtService;

    public RecebimentoController(RecebimentoService recebimentoService, JwtService jwtService) {
        this.recebimentoService = recebimentoService;
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
    public ResponseEntity<List<Recebimento>> buscarTodos(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Recebimento> todosOsRecebimentos = recebimentoService.findAllByOwnerId(userId);
        return ResponseEntity.ok(todosOsRecebimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recebimento> buscarPorId(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Recebimento recebimentoPorId = recebimentoService.getById(id, userId);
        return ResponseEntity.ok(recebimentoPorId);
    }

    @PostMapping
    public ResponseEntity<Recebimento> salvar(@RequestBody Recebimento recebimento, HttpServletRequest request)
            throws IllegalArgumentException, EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        recebimento.setUsuarioId(userId);
        Recebimento novoRecebimento = recebimentoService.insert(recebimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRecebimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recebimento> atualizar(@PathVariable("id") Long id, @RequestBody Recebimento recebimento, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        recebimento.setId(id);
        recebimento.setUsuarioId(userId);
        Recebimento recebimentoParaAtualizar = recebimentoService.update(userId, recebimento);
        return ResponseEntity.ok(recebimentoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        recebimentoService.remove(id, userId);
        return ResponseEntity.noContent().build();
    }
}