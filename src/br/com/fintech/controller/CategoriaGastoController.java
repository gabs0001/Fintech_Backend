package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.service.CategoriaGastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-gastos")
public class CategoriaGastoController {
    private final CategoriaGastoService categoriaGastoService;

    public CategoriaGastoController(CategoriaGastoService categoriaGastoService) {
        this.categoriaGastoService = categoriaGastoService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaGasto>> buscarTodos() {
        List<CategoriaGasto> todasAsCategorias = categoriaGastoService.getAll();
        return ResponseEntity.ok(todasAsCategorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaGasto> buscarPorId(@PathVariable("id") Long id) {
        CategoriaGasto categoriaPorId = categoriaGastoService.getById(id);

        return ResponseEntity.ok(categoriaPorId);
    }

    @PostMapping
    public ResponseEntity<CategoriaGasto> salvar(@RequestBody CategoriaGasto categoria) throws EntityNotFoundException {
        CategoriaGasto novaCategoria = categoriaGastoService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaGasto> atualizar(@PathVariable("id") Long id, @RequestBody CategoriaGasto categoria)
            throws EntityNotFoundException
    {
        categoria.setId(id);

        CategoriaGasto categoriaParaAtualizar = categoriaGastoService.update(id, categoria);
        return ResponseEntity.ok(categoriaParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException {
        categoriaGastoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}