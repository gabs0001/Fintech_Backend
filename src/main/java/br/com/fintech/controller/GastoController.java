package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Gasto;
import br.com.fintech.service.GastoService;
import br.com.fintech.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {
    private final GastoService gastoService;
    private final JwtService jwtService;

    public GastoController(GastoService gastoService, JwtService jwtService) {
        this.gastoService = gastoService;
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
    public ResponseEntity<List<Gasto>> buscarTodos(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Gasto> todosOsGastos = gastoService.findAllByOwnerId(userId);
        return ResponseEntity.ok(todosOsGastos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> buscarPorId(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Gasto gastoPorId = gastoService.getById(id, userId);
        return ResponseEntity.ok(gastoPorId);
    }

    @PostMapping
    public ResponseEntity<Gasto> salvar(@RequestBody Gasto gasto, HttpServletRequest request)
            throws IllegalArgumentException, EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        gasto.setUsuarioId(userId);
        Gasto novoGasto = gastoService.insert(gasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoGasto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gasto> atualizar(@PathVariable("id") Long id, @RequestBody Gasto gasto, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        gasto.setId(id);
        gasto.setUsuarioId(userId);
        Gasto gastoAtualizado = gastoService.update(userId, gasto);
        return ResponseEntity.ok(gastoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        gastoService.remove(id, userId);
        return ResponseEntity.noContent().build();
    }
}