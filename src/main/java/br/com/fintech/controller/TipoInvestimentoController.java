package br.com.fintech.controller;

import br.com.fintech.dto.TipoInvestimentoDTO;
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
    public ResponseEntity<List<TipoInvestimentoDTO>> buscarTodos() {
        List<TipoInvestimento> todasAsCategorias = tipoInvestimentoService.getAll();
        List<TipoInvestimentoDTO> categoriasDTO = todasAsCategorias.stream()
                .map(TipoInvestimentoDTO::new)
                .toList();

        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoInvestimentoDTO> buscarPorId(@PathVariable("id") Long id) {
        TipoInvestimento categoriaPorId = tipoInvestimentoService.getById(id);
        return ResponseEntity.ok(new TipoInvestimentoDTO(categoriaPorId));
    }

    @PostMapping
    public ResponseEntity<TipoInvestimentoDTO> salvar(@RequestBody TipoInvestimento categoria) throws EntityNotFoundException {
        TipoInvestimento novaCategoria = tipoInvestimentoService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TipoInvestimentoDTO(novaCategoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoInvestimentoDTO> atualizar(@PathVariable("id") Long id, @RequestBody TipoInvestimento categoria)
            throws EntityNotFoundException {
        categoria.setId(id);
        TipoInvestimento categoriaAtualizada = tipoInvestimentoService.update(id, categoria);
        return ResponseEntity.ok(new TipoInvestimentoDTO(categoriaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException {
        tipoInvestimentoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}