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

    @GetMapping
    public ResponseEntity<List<ObjetivoFinanceiro>> buscarTodos(@RequestParam("userId") Long userId) {
        try {
            List<ObjetivoFinanceiro> todosOsObjetivosFinanceiros = objetivoFinanceiroService.getAllByUserId(userId);
            return ResponseEntity.ok(todosOsObjetivosFinanceiros);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar todos os objetivos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> buscarPorId(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            ObjetivoFinanceiro objetivoPorId = objetivoFinanceiroService.getById(id, userId);

            if(objetivoPorId == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(objetivoPorId);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar os objetivos no id especificado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ObjetivoFinanceiro> salvar(@RequestBody ObjetivoFinanceiro objetivoFinanceiro) {
        try {
            ObjetivoFinanceiro novoObjetivoFinanceiro = objetivoFinanceiroService.insert(objetivoFinanceiro);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoObjetivoFinanceiro);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar inserir novo objetivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoFinanceiro> atualizar(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId,
            @RequestBody ObjetivoFinanceiro objetivoFinanceiro
    ) {
        try {
            objetivoFinanceiro.setId(id);

            ObjetivoFinanceiro objetivoParaAtualizar = objetivoFinanceiroService.update(objetivoFinanceiro, userId);

            return ResponseEntity.ok(objetivoParaAtualizar);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar atualizar o objetivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            objetivoFinanceiroService.remove(id, userId);
            return ResponseEntity.noContent().build();
        }
        catch(EntityNotFoundException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar excluir o objetivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}