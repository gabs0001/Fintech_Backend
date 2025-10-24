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

    private Long getAuthenticatedUserId() {
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<Recebimento>> buscarTodos() throws SQLException {
        Long userId = getAuthenticatedUserId();

        List<Recebimento> todosOsRecebimentos = recebimentoService.getAllByUserId(userId);

        return ResponseEntity.ok(todosOsRecebimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recebimento> buscarPorId(@PathVariable("id") Long id)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();

        Recebimento recebimentoPorId = recebimentoService.getById(id, userId);

        if(recebimentoPorId == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(recebimentoPorId);
    }

    @PostMapping
    public ResponseEntity<Recebimento> salvar(@RequestBody Recebimento recebimento)
            throws SQLException, IllegalArgumentException, EntityNotFoundException
    {
        Long userId = getAuthenticatedUserId();
        recebimento.setUsuarioId(userId);

        Recebimento novoRecebimento = recebimentoService.insert(recebimento);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoRecebimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recebimento> atualizar(@PathVariable("id") Long id, @RequestBody Recebimento recebimento)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();

        recebimento.setId(id);
        recebimento.setUsuarioId(userId);

        Recebimento recebimentoParaAtualizar = recebimentoService.update(userId, recebimento);

        return ResponseEntity.ok(recebimentoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id)
            throws SQLException, EntityNotFoundException, IllegalArgumentException
    {
        Long userId = getAuthenticatedUserId();

        recebimentoService.remove(id, userId);

        return ResponseEntity.noContent().build();
    }
}