package br.com.fintech.controller;

import br.com.fintech.dto.InvestimentoDTO;
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
    public ResponseEntity<List<InvestimentoDTO>> buscarTodos(HttpServletRequest request) {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Investimento> todosOsInvestimentos = investimentoService.findAllByOwnerId(userId);
        List<InvestimentoDTO> investimentosDTO = todosOsInvestimentos.stream()
                .map(InvestimentoDTO::new)
                .toList();

        return ResponseEntity.ok(investimentosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestimentoDTO> buscarPorId(@PathVariable("id") Long id, HttpServletRequest request)
            throws EntityNotFoundException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Investimento investimentoPorId = investimentoService.getById(id, userId);
        return ResponseEntity.ok(new InvestimentoDTO(investimentoPorId));
    }

    @PostMapping
    public ResponseEntity<InvestimentoDTO> salvar(@RequestBody Investimento investimento, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        investimento.setUsuarioId(userId);
        Investimento novoInvestimento = investimentoService.insert(investimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(new InvestimentoDTO(novoInvestimento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestimentoDTO> atualizar(@PathVariable("id") Long id, @RequestBody Investimento investimento, HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        investimento.setId(id);
        investimento.setUsuarioId(userId);
        Investimento investimentoAtualizado = investimentoService.update(userId, investimento);
        return ResponseEntity.ok(new InvestimentoDTO(investimentoAtualizado));
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