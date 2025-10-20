package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.RecebimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/recebimentos")
public class RecebimentoController {
    private final RecebimentoService recebimentoService;

    public RecebimentoController(RecebimentoService recebimentoService) {
        this.recebimentoService = recebimentoService;
    }

    @GetMapping
    public ResponseEntity<List<Recebimento>> buscarTodos(@RequestParam("userId") Long userId) {
        try {
            List<Recebimento> todosOsRecebimentos = recebimentoService.getAllByUserId(userId);
            return ResponseEntity.ok(todosOsRecebimentos);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar todos os recebimentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recebimento> buscarPorId(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            Recebimento recebimentoPorId = recebimentoService.getById(id, userId);

            if(recebimentoPorId == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(recebimentoPorId);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar os recebimentos no id especificado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Recebimento> salvar(@RequestBody Recebimento recebimento) {
        try {
            Recebimento novoRecebimento = recebimentoService.insert(recebimento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoRecebimento);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar inserir novo recebimento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recebimento> atualizar(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId,
            @RequestBody Recebimento recebimento
    ) {
        try {
            recebimento.setId(id);

            Recebimento recebimentoParaAtualizar = recebimentoService.update(recebimento, userId);

            return ResponseEntity.ok(recebimentoParaAtualizar);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar atualizar o recebimento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            recebimentoService.remove(id, userId);
            return ResponseEntity.noContent().build();
        }
        catch(EntityNotFoundException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar excluir o recebimento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}