package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Categoria;
import br.com.fintech.service.CategoriaBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaBaseService categoriaService;

    public CategoriaController(CategoriaBaseService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> buscarTodos() throws SQLException {
        List<Categoria> todasAsCategorias = categoriaService.getAll();
        return ResponseEntity.ok(todasAsCategorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable("id") Long id) throws SQLException {
        Categoria categoriaPorId = categoriaService.getById(id);

        if(categoriaPorId == null) {
            ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoriaPorId);
    }

    @PostMapping
    public ResponseEntity<Categoria> salvar(@RequestBody Categoria categoria) throws SQLException, EntityNotFoundException {
        Categoria novaCategoria = categoriaService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable("id") Long id, @RequestBody Categoria categoria) throws SQLException, EntityNotFoundException {
        Categoria categoriaParaAtualizar = categoriaService.update(id, categoria);
        return ResponseEntity.ok(categoriaParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws SQLException, EntityNotFoundException {
        categoriaService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
