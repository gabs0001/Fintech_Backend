package br.com.fintech.controller;

import br.com.fintech.dto.InstituicaoDTO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Instituicao;
import br.com.fintech.service.InstituicaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instituicoes")
public class InstituicaoController {
    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    @GetMapping
    public ResponseEntity<List<InstituicaoDTO>> buscarTodos() {
        List<Instituicao> todasAsInstituicoes = instituicaoService.getAll();
        List<InstituicaoDTO> instituicoesDTO = todasAsInstituicoes.stream()
                .map(InstituicaoDTO::new)
                .toList();

        return ResponseEntity.ok(instituicoesDTO);
    }

    @GetMapping("{id}")
    public ResponseEntity<InstituicaoDTO> buscarPorId(@PathVariable Long id) {
        Instituicao instituicaoPorId = instituicaoService.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instituição com id: " + id + " não encontrada!"));

        return ResponseEntity.ok(new InstituicaoDTO(instituicaoPorId));
    }

    @PostMapping
    public ResponseEntity<InstituicaoDTO> salvar(@RequestBody Instituicao instituicao) throws EntityNotFoundException {
        Instituicao novaInstituicao = instituicaoService.insert(instituicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(new InstituicaoDTO(novaInstituicao));
    }

    @PutMapping("{id}")
    public ResponseEntity<InstituicaoDTO> atualizar(@PathVariable("id") Long id, @RequestBody Instituicao instituicao) throws EntityNotFoundException {
        Instituicao instituicaoAtualizada = instituicaoService.update(id, instituicao);
        return ResponseEntity.ok(new InstituicaoDTO(instituicaoAtualizada));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException {
        instituicaoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}