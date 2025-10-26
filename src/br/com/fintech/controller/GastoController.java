package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Gasto;
import br.com.fintech.service.GastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {
    private final GastoService gastoService;
    private static final Long MOCK_USER_ID = 1L;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @GetMapping
    public ResponseEntity<List<Gasto>> buscarTodos() {
        Long userId = MOCK_USER_ID;

        List<Gasto> todosOsGastos = gastoService.findAllByOwnerId(userId);

        return ResponseEntity.ok(todosOsGastos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> buscarPorId(@PathVariable("id") Long id) throws EntityNotFoundException {
        Long userId = MOCK_USER_ID;

        Gasto gastoPorId = gastoService.getById(id, userId);

        return ResponseEntity.ok(gastoPorId);
    }

    @PostMapping
    public ResponseEntity<Gasto> salvar(@RequestBody Gasto gasto)
            throws IllegalArgumentException, EntityNotFoundException
    {
        Long userId = MOCK_USER_ID;

        gasto.setUsuarioId(userId);

        Gasto novoGasto = gastoService.insert(gasto);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoGasto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gasto> atualizar(@PathVariable("id") Long id, @RequestBody Gasto gasto)
            throws EntityNotFoundException, IllegalArgumentException
    {
        Long userId = MOCK_USER_ID;

        gasto.setId(id);
        gasto.setUsuarioId(userId);

        Gasto gastoAtualizado = gastoService.update(userId, gasto);

        return ResponseEntity.ok(gastoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id)
            throws EntityNotFoundException, IllegalArgumentException
    {
        Long userId = MOCK_USER_ID;

        gastoService.remove(id, userId);

        return ResponseEntity.noContent().build();
    }
}