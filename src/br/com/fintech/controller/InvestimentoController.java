package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Investimento;
import br.com.fintech.service.InvestimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/investimentos")
public class InvestimentoController {
    private final InvestimentoService investimentoService;

    public InvestimentoController(InvestimentoService investimentoService) {
        this.investimentoService = investimentoService;
    }

    private Long getAuthenticatedUserId() {
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<Investimento>> buscarTodos() throws SQLException {
        Long userId = getAuthenticatedUserId();

        List<Investimento> todosOsInvestimentos = investimentoService.getAllByUserId(userId);

        return ResponseEntity.ok(todosOsInvestimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investimento> buscarPorId(@PathVariable("id") Long id)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();

        Investimento investimentoPorId = investimentoService.getById(id, userId);

        if(investimentoPorId == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(investimentoPorId);
    }

    @PostMapping
    public ResponseEntity<Investimento> salvar(@RequestBody Investimento investimento)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();
        investimento.setUsuarioId(userId);

        Investimento novoInvestimento = investimentoService.insert(investimento);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoInvestimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investimento> atualizar(@PathVariable("id") Long id, @RequestBody Investimento investimento) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        Long userId = getAuthenticatedUserId();

        investimento.setId(id);
        investimento.setUsuarioId(userId);

        Investimento investimentoParaAtualizar = investimentoService.update(userId, investimento);

        return ResponseEntity.ok(investimentoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        Long userId = getAuthenticatedUserId();

        investimentoService.remove(id, userId);

        return ResponseEntity.noContent().build();
    }
}