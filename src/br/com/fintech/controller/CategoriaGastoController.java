package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.service.CategoriaGastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/categorias-gastos")
public class CategoriaGastoController {
    private final CategoriaGastoService categoriaGastoService;

    public CategoriaGastoController(CategoriaGastoService categoriaGastoService) {
        this.categoriaGastoService = categoriaGastoService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaGasto>> buscarTodos() throws SQLException {
        List<CategoriaGasto> todasAsCategorias = categoriaGastoService.getAll();
        return ResponseEntity.ok(todasAsCategorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaGasto> buscarPorId(@PathVariable("id") Long id) throws SQLException {
        CategoriaGasto categoriaPorId = categoriaGastoService.getById(id);

        if(categoriaPorId == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoriaPorId);
    }

    @PostMapping
    public ResponseEntity<CategoriaGasto> salvar(@RequestBody CategoriaGasto categoria) throws SQLException, EntityNotFoundException {
        CategoriaGasto novaCategoria = categoriaGastoService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaGasto> atualizar(@PathVariable("id") Long id, @RequestBody CategoriaGasto categoria) throws SQLException, EntityNotFoundException {
        CategoriaGasto categoriaParaAtualizar = categoriaGastoService.update(id, categoria);
        return ResponseEntity.ok(categoriaParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws SQLException, EntityNotFoundException {
        categoriaGastoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
