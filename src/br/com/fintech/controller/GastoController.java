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

    private Long getAuthenticatedUserId() {
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<Gasto>> buscarTodos() throws SQLException {
        Long userId = getAuthenticatedUserId();

        List<Gasto> todosOsGastos = gastoService.getAllByUserId(userId);

        return ResponseEntity.ok(todosOsGastos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> buscarPorId(@PathVariable("id") Long id)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();

        Gasto gastoPorId = gastoService.getById(id, userId);

        if(gastoPorId == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(gastoPorId);
    }

    @PostMapping
    public ResponseEntity<Gasto> salvar(@RequestBody Gasto gasto)
            throws SQLException, IllegalArgumentException, EntityNotFoundException
    {
        Long userId = getAuthenticatedUserId();
        gasto.setUsuarioId(userId);

        Gasto novoGasto = gastoService.insert(gasto);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoGasto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gasto> atualizar(@PathVariable("id") Long id, @RequestBody Gasto gasto)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();

        gasto.setId(id);
        gasto.setUsuarioId(userId);

        Gasto gastoAtualizado = gastoService.update(userId, gasto);

        return ResponseEntity.ok(gastoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();

        gastoService.remove(id, userId);

        return ResponseEntity.noContent().build();
    }
}