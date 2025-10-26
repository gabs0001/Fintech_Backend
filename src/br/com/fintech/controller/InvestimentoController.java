package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Investimento;
import br.com.fintech.service.InvestimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investimentos")
public class InvestimentoController {
    private final InvestimentoService investimentoService;
    private static final Long MOCK_USER_ID = 1L;

    public InvestimentoController(InvestimentoService investimentoService) {
        this.investimentoService = investimentoService;
    }

    @GetMapping
    public ResponseEntity<List<Investimento>> buscarTodos() {
        Long userId = MOCK_USER_ID;

        List<Investimento> todosOsInvestimentos = investimentoService.findAllByOwnerId(userId);

        return ResponseEntity.ok(todosOsInvestimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investimento> buscarPorId(@PathVariable("id") Long id) throws EntityNotFoundException {
        Long userId = MOCK_USER_ID;

        Investimento investimentoPorId = investimentoService.getById(id, userId);

        return ResponseEntity.ok(investimentoPorId);
    }

    @PostMapping
    public ResponseEntity<Investimento> salvar(@RequestBody Investimento investimento)
            throws EntityNotFoundException, IllegalArgumentException
    {
        Long userId = MOCK_USER_ID;
        investimento.setUsuarioId(userId);

        Investimento novoInvestimento = investimentoService.insert(investimento);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoInvestimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investimento> atualizar(@PathVariable("id") Long id, @RequestBody Investimento investimento)
            throws EntityNotFoundException, IllegalArgumentException
    {
        Long userId = MOCK_USER_ID;

        investimento.setId(id);
        investimento.setUsuarioId(userId);

        Investimento investimentoParaAtualizar = investimentoService.update(userId, investimento);

        return ResponseEntity.ok(investimentoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id)
            throws EntityNotFoundException, IllegalArgumentException
    {
        Long userId = MOCK_USER_ID;

        investimentoService.remove(id, userId);

        return ResponseEntity.noContent().build();
    }
}