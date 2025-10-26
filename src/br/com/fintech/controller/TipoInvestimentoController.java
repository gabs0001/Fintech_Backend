package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.TipoInvestimento;
import br.com.fintech.service.TipoInvestimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-investimentos")
public class TipoInvestimentoController {
    private final TipoInvestimentoService tipoInvestimentoService;

    public TipoInvestimentoController(TipoInvestimentoService tipoInvestimentoService) {
        this.tipoInvestimentoService = tipoInvestimentoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoInvestimento>> buscarTodos() {
        List<TipoInvestimento> todasAsCategorias = tipoInvestimentoService.getAll();
        return ResponseEntity.ok(todasAsCategorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoInvestimento> buscarPorId(@PathVariable("id") Long id) {
        TipoInvestimento categoriaPorId = tipoInvestimentoService.getById(id);

        return ResponseEntity.ok(categoriaPorId);
    }

    @PostMapping
    public ResponseEntity<TipoInvestimento> salvar(@RequestBody TipoInvestimento categoria) throws EntityNotFoundException {
        TipoInvestimento novaCategoria = tipoInvestimentoService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoInvestimento> atualizar(@PathVariable("id") Long id, @RequestBody TipoInvestimento categoria)
            throws EntityNotFoundException
    {
        categoria.setId(id);

        TipoInvestimento categoriaParaAtualizar = tipoInvestimentoService.update(id, categoria);
        return ResponseEntity.ok(categoriaParaAtualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException {
        tipoInvestimentoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}