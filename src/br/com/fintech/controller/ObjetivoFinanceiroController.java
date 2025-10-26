package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.ObjetivoFinanceiro;
import br.com.fintech.service.ObjetivoFinanceiroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objetivos-financeiros")
public class ObjetivoFinanceiroController {
    private final ObjetivoFinanceiroService objetivoFinanceiroService;
    private static final Long MOCK_USER_ID = 1L;

    public ObjetivoFinanceiroController(ObjetivoFinanceiroService objetivoFinanceiroService) {
        this.objetivoFinanceiroService = objetivoFinanceiroService;
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoFinanceiro>> buscarTodos() {
        Long userId = MOCK_USER_ID;

        List<ObjetivoFinanceiro> todosOsObjetivosFinanceiros = objetivoFinanceiroService.findAllByOwnerId(userId);

        return ResponseEntity.ok(todosOsObjetivosFinanceiros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> buscarPorId(@PathVariable("id") Long id) throws EntityNotFoundException {
        Long userId = MOCK_USER_ID;

        ObjetivoFinanceiro objetivoPorId = objetivoFinanceiroService.getById(id, userId);

        return ResponseEntity.ok(objetivoPorId);
    }

    @PostMapping
    public ResponseEntity<ObjetivoFinanceiro> salvar(@RequestBody ObjetivoFinanceiro objetivoFinanceiro) throws EntityNotFoundException, IllegalArgumentException {
        Long userId = MOCK_USER_ID;
        objetivoFinanceiro.setUsuarioId(userId);

        ObjetivoFinanceiro novoObjetivoFinanceiro = objetivoFinanceiroService.insert(objetivoFinanceiro);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoObjetivoFinanceiro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> atualizar(
            @PathVariable("id") Long id,
            @RequestBody ObjetivoFinanceiro objetivoFinanceiro
    ) throws IllegalArgumentException, EntityNotFoundException
    {
        Long userId = MOCK_USER_ID;

        objetivoFinanceiro.setId(id);
        objetivoFinanceiro.setUsuarioId(userId);

        ObjetivoFinanceiro objetivoParaAtualizar = objetivoFinanceiroService.update(userId, objetivoFinanceiro);

        return ResponseEntity.ok(objetivoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalArgumentException {
        Long userId = MOCK_USER_ID;

        objetivoFinanceiroService.deleteByIdAndOwnerId(id, userId);

        return ResponseEntity.noContent().build();
    }
}