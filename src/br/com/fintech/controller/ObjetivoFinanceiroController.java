package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.ObjetivoFinanceiro;
import br.com.fintech.service.ObjetivoFinanceiroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/objetivos-financeiros")
public class ObjetivoFinanceiroController {
    private final ObjetivoFinanceiroService objetivoFinanceiroService;

    public ObjetivoFinanceiroController(ObjetivoFinanceiroService objetivoFinanceiroService) {
        this.objetivoFinanceiroService = objetivoFinanceiroService;
    }

    private Long getAuthenticatedUserId() {
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoFinanceiro>> buscarTodos() throws SQLException {
        Long userId = getAuthenticatedUserId();

        List<ObjetivoFinanceiro> todosOsObjetivosFinanceiros = objetivoFinanceiroService.getAllByUserId(userId);

        return ResponseEntity.ok(todosOsObjetivosFinanceiros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> buscarPorId(@PathVariable("id") Long id) throws SQLException, IllegalArgumentException, EntityNotFoundException {
        Long userId = getAuthenticatedUserId();

        ObjetivoFinanceiro objetivoPorId = objetivoFinanceiroService.getById(id, userId);

        if(objetivoPorId == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(objetivoPorId);
    }

    @PostMapping
    public ResponseEntity<ObjetivoFinanceiro> salvar(@RequestBody ObjetivoFinanceiro objetivoFinanceiro) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        Long userId = getAuthenticatedUserId();
        objetivoFinanceiro.setUsuarioId(userId);

        ObjetivoFinanceiro novoObjetivoFinanceiro = objetivoFinanceiroService.insert(objetivoFinanceiro);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoObjetivoFinanceiro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> atualizar(@PathVariable("id") Long id, @RequestBody ObjetivoFinanceiro objetivoFinanceiro) throws SQLException, IllegalArgumentException, EntityNotFoundException {
        Long userId = getAuthenticatedUserId();

        objetivoFinanceiro.setId(id);
        objetivoFinanceiro.setUsuarioId(userId);

        ObjetivoFinanceiro objetivoParaAtualizar = objetivoFinanceiroService.update(userId, objetivoFinanceiro);

        return ResponseEntity.ok(objetivoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        Long userId = getAuthenticatedUserId();

        objetivoFinanceiroService.remove(id, userId);

        return ResponseEntity.noContent().build();
    }
}