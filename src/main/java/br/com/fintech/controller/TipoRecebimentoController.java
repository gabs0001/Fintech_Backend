package br.com.fintech.controller;

import br.com.fintech.dto.TipoRecebimentoDTO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.TipoRecebimento;
import br.com.fintech.service.TipoRecebimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-recebimentos")
public class TipoRecebimentoController {
    private final TipoRecebimentoService tipoRecebimentoService;

    public TipoRecebimentoController(TipoRecebimentoService tipoRecebimentoService) {
        this.tipoRecebimentoService = tipoRecebimentoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoRecebimentoDTO>> buscarTodos() {
        List<TipoRecebimento> todasAsCategorias = tipoRecebimentoService.getAll();
        List<TipoRecebimentoDTO> categoriasDTO = todasAsCategorias.stream()
                .map(TipoRecebimentoDTO::new)
                .toList();

        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoRecebimentoDTO> buscarPorId(@PathVariable("id") Long id) {
        TipoRecebimento categoriaPorId = tipoRecebimentoService.getById(id);
        return ResponseEntity.ok(new TipoRecebimentoDTO(categoriaPorId));
    }

    @PostMapping
    public ResponseEntity<TipoRecebimentoDTO> salvar(@RequestBody TipoRecebimento categoria) throws EntityNotFoundException {
        TipoRecebimento novaCategoria = tipoRecebimentoService.insert(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TipoRecebimentoDTO(novaCategoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoRecebimentoDTO> atualizar(@PathVariable("id") Long id, @RequestBody TipoRecebimento categoria)
            throws EntityNotFoundException {
        categoria.setId(id);
        TipoRecebimento categoriaAtualizada = tipoRecebimentoService.update(id, categoria);
        return ResponseEntity.ok(new TipoRecebimentoDTO(categoriaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException {
        tipoRecebimentoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}