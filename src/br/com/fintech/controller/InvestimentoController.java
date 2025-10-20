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

    @GetMapping
    public ResponseEntity<List<Investimento>> buscarTodos(@RequestParam("userId") Long userId) {
        try {
            List<Investimento> todosOsInvestimentos = investimentoService.getAllByUserId(userId);
            return ResponseEntity.ok(todosOsInvestimentos);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar todos os investimentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investimento> buscarPorId(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            Investimento investimentoPorId = investimentoService.getById(id, userId);

            if(investimentoPorId == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(investimentoPorId);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar os investimentos no id especificado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Investimento> salvar(@RequestBody Investimento investimento) {
        try {
            Investimento novoInvestimento = investimentoService.insert(investimento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoInvestimento);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar inserir novo investimento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investimento> atualizar(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId,
            @RequestBody Investimento investimento
    ) {
        try {
            investimento.setId(id);

            Investimento investimentoParaAtualizar = investimentoService.update(investimento, userId);

            return ResponseEntity.ok(investimentoParaAtualizar);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar atualizar o investimento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            investimentoService.remove(id, userId);
            return ResponseEntity.noContent().build();
        }
        catch(EntityNotFoundException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar excluir o investimento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}