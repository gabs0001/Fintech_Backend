package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;
import br.com.fintech.service.RecebimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recebimentos")
public class RecebimentoController {
    private final RecebimentoService recebimentoService;
    private static final Long MOCK_USER_ID = 1L;

    public RecebimentoController(RecebimentoService recebimentoService) {
        this.recebimentoService = recebimentoService;
    }

    @GetMapping
    public ResponseEntity<List<Recebimento>> buscarTodos() {
        Long userId = MOCK_USER_ID;

        List<Recebimento> todosOsRecebimentos = recebimentoService.findAllByOwnerId(userId);

        return ResponseEntity.ok(todosOsRecebimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recebimento> buscarPorId(@PathVariable("id") Long id) throws EntityNotFoundException {
        Long userId = MOCK_USER_ID;

        Recebimento recebimentoPorId = recebimentoService.getById(id, userId);

        return ResponseEntity.ok(recebimentoPorId);
    }

    @PostMapping
    public ResponseEntity<Recebimento> salvar(@RequestBody Recebimento recebimento)
            throws IllegalArgumentException, EntityNotFoundException
    {
        Long userId = MOCK_USER_ID;
        recebimento.setUsuarioId(userId);

        Recebimento novoRecebimento = recebimentoService.insert(recebimento);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoRecebimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recebimento> atualizar(@PathVariable("id") Long id, @RequestBody Recebimento recebimento)
            throws EntityNotFoundException, IllegalArgumentException
    {
        Long userId = MOCK_USER_ID;

        recebimento.setId(id);
        recebimento.setUsuarioId(userId);

        Recebimento recebimentoParaAtualizar = recebimentoService.update(userId, recebimento);

        return ResponseEntity.ok(recebimentoParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id)
            throws EntityNotFoundException, IllegalArgumentException
    {
        Long userId = MOCK_USER_ID;

        recebimentoService.remove(id, userId);

        return ResponseEntity.noContent().build();
    }
}