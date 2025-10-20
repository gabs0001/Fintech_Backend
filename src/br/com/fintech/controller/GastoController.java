package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Gasto;
import br.com.fintech.service.GastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {
    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @GetMapping
    public ResponseEntity<List<Gasto>> buscarTodos(@RequestParam("id") Long id) {
        try {
            List<Gasto> todosOsGastos = gastoService.getAllByUserId(id);
            return ResponseEntity.ok(todosOsGastos);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar todos os gastos do usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> buscarPorId(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            Gasto gastoPorId = gastoService.getById(id, userId);

            if(gastoPorId == null) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(gastoPorId);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar buscar os gastos no id especificado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Gasto> salvar(@RequestBody Gasto gasto) {
        try {
            Gasto novoGasto = gastoService.insert(gasto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoGasto);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar inserir novo gasto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gasto> atualizar(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId,
            @RequestBody Gasto gasto
    ) throws EntityNotFoundException {
        try {
            gasto.setId(id);

            Gasto gastoParaAtualizar = gastoService.update(gasto, userId);

            return ResponseEntity.ok(gastoParaAtualizar);
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar atualizar o gasto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        try {
            gastoService.remove(id, userId);
            return ResponseEntity.noContent().build();
        }
        catch(EntityNotFoundException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch(SQLException e) {
            System.err.println("Erro ao tentar remover o gasto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
