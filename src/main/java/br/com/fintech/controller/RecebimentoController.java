package br.com.fintech.controller;

import br.com.fintech.dto.RecebimentoDTO;
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
    public ResponseEntity<List<RecebimentoDTO>> buscarTodos(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Recebimento> todosOsRecebimentos = recebimentoService.findAllByOwnerId(userId);
        List<RecebimentoDTO> recebimentosDTO = todosOsRecebimentos.stream()
                .map(RecebimentoDTO::new)
                .toList();

        return ResponseEntity.ok(recebimentosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecebimentoDTO> buscarPorId(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Recebimento recebimentoPorId = recebimentoService.getById(id, userId);
        return ResponseEntity.ok(new RecebimentoDTO(recebimentoPorId));
    }

    @PostMapping
    public ResponseEntity<RecebimentoDTO> salvar(@RequestBody Recebimento recebimento, HttpServletRequest request)
            throws IllegalArgumentException, EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        recebimento.setUsuarioId(userId);
        Recebimento novoRecebimento = recebimentoService.insert(recebimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RecebimentoDTO(novoRecebimento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecebimentoDTO> atualizar(@PathVariable("id") Long id, @RequestBody Recebimento recebimento, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        recebimento.setId(id);
        recebimento.setUsuarioId(userId);
        Recebimento recebimentoAtualizado = recebimentoService.update(userId, recebimento);
        return ResponseEntity.ok(new RecebimentoDTO(recebimentoAtualizado));
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