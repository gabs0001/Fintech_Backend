package br.com.fintech.controller;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Instituicao;
import br.com.fintech.service.InstituicaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/instituicoes")
public class InstituicaoController {
    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    @GetMapping
    public ResponseEntity<List<Instituicao>> buscarTodos() {
        List<Instituicao> todasAsInstituicoes = instituicaoService.getAll();
        return ResponseEntity.ok(todasAsInstituicoes);
    }

    @GetMapping("{id}")
    public ResponseEntity<Instituicao> buscarPorId(@PathVariable Long id) {
        Instituicao instituicaoPorId = instituicaoService.getById(id);

        if(instituicaoPorId == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(instituicaoPorId);
    }

    @PostMapping
    public ResponseEntity<Instituicao> salvar(@RequestBody Instituicao instituicao) throws EntityNotFoundException {
        Instituicao novaInstituicao = instituicaoService.insert(instituicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaInstituicao);
    }

    @PutMapping("{id}")
    public ResponseEntity<Instituicao> atualizar(@PathVariable("id") Long id, @RequestBody Instituicao instituicao) throws EntityNotFoundException {
        Instituicao instituicaoParaAtualizar = instituicaoService.update(id, instituicao);
        return ResponseEntity.ok(instituicaoParaAtualizar);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") Long id) throws EntityNotFoundException {
        instituicaoService.remove(id);
        return ResponseEntity.noContent().build();
    }
}