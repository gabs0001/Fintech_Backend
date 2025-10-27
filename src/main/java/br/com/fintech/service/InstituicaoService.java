package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Instituicao;
import br.com.fintech.repository.InstituicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstituicaoService {
    private final InstituicaoRepository repository;

    public InstituicaoService(InstituicaoRepository repository) {
        this.repository = repository;
    }

    private void validarInstituicao(Instituicao instituicao) throws IllegalArgumentException {
        if(instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da instituição é obrigatório e não pode estar em branco!");
        }
    }

    public Instituicao fetchOrThrowException(Long instituicaoId) throws EntityNotFoundException {
        return repository.findById(instituicaoId).orElseThrow(() ->
                new EntityNotFoundException("Instituição com ID: " + instituicaoId + " não encontrada!")
        );
    }

    public List<Instituicao> getAll() {
        return repository.findAll();
    }

    public Optional<Instituicao> getById(Long idEntity) {
        return repository.findById(idEntity);
    }

    public Instituicao insert(Instituicao novaInstituicao) throws IllegalArgumentException {
        validarInstituicao(novaInstituicao);
        return repository.save(novaInstituicao);
    }

    public Instituicao update(Long idEntity, Instituicao instituicaoParaAlterar) throws EntityNotFoundException, IllegalArgumentException {
        validarInstituicao(instituicaoParaAlterar);

        if(instituicaoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID da instituicao a ser atualizado é obrigatório.");
        }

        fetchOrThrowException(idEntity);

        instituicaoParaAlterar.setId(idEntity);

        return repository.save(instituicaoParaAlterar);
    }

    public void remove(Long id) throws EntityNotFoundException {
        fetchOrThrowException(id);
        repository.deleteById(id);
    }
}