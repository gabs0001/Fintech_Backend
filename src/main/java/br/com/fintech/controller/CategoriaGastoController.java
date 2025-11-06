package br.com.fintech.controller;

import br.com.fintech.dto.CategoriaGastoDTO;
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
    public ResponseEntity<List<CategoriaGastoDTO>> buscarTodos() {
        List<CategoriaGasto> todasAsCategorias = categoriaGastoService.getAll();
        List<CategoriaGastoDTO> categoriasDTO = todasAsCategorias.stream()
                .map(CategoriaGastoDTO::new)
                .toList();

        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaGastoDTO> buscarPorId(@PathVariable("id") Long id) {
        CategoriaGasto categoriaPorId = categoriaGastoService.getById(id);
        return ResponseEntity.ok(new CategoriaGastoDTO(categoriaPorId));
    }

    @PostMapping
    public ResponseEntity<CategoriaGastoDTO> salvar(@RequestBody CategoriaGasto categoria) throws EntityNotFoundException {
        CategoriaGasto novaCategoria = categoriaGastoService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CategoriaGastoDTO(novaCategoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaGastoDTO> atualizar(@PathVariable("id") Long id, @RequestBody CategoriaGasto categoria)
            throws EntityNotFoundException {
        categoria.setId(id);
        CategoriaGasto categoriaAtualizada = categoriaGastoService.update(id, categoria);
        return ResponseEntity.ok(new CategoriaGastoDTO(categoriaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException {
        categoriaGastoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}