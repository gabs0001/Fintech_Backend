package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.TipoRecebimento;
import br.com.fintech.service.TipoRecebimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-recebimentos")
public class TipoRecebimentoController {
    private final TipoRecebimentoService tipoRecebimentoService;

    public TipoRecebimentoController(TipoRecebimentoService tipoRecebimentoService) {
        this.tipoRecebimentoService = tipoRecebimentoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoRecebimento>> buscarTodos() throws SQLException {
        List<TipoRecebimento> todasAsCategorias = tipoRecebimentoService.getAll();
        return ResponseEntity.ok(todasAsCategorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoRecebimento> buscarPorId(@PathVariable("id") Long id) throws SQLException {
        TipoRecebimento categoriaPorId = tipoRecebimentoService.getById(id);

        if(categoriaPorId == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoriaPorId);
    }

    @PostMapping
    public ResponseEntity<TipoRecebimento> salvar(@RequestBody TipoRecebimento categoria) throws SQLException, EntityNotFoundException {
        TipoRecebimento novaCategoria = tipoRecebimentoService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoRecebimento> atualizar(@PathVariable("id") Long id, @RequestBody TipoRecebimento categoria) throws SQLException, EntityNotFoundException {
        TipoRecebimento categoriaParaAtualizar = tipoRecebimentoService.update(id, categoria);
        return ResponseEntity.ok(categoriaParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws SQLException, EntityNotFoundException {
        tipoRecebimentoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}