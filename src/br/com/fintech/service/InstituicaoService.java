package br.com.fintech.service;

import br.com.fintech.dao.InstituicaoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Instituicao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class InstituicaoService {
    private final InstituicaoDAO dao;

    public InstituicaoService(InstituicaoDAO dao) {
        this.dao = dao;
    }

    private void validarInstituicao(Instituicao instituicao) throws IllegalArgumentException {
        if(instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da instituição é obrigatório e não pode estar em branco!");
        }
    }

    public Instituicao fetchOrThrowException(Long instituicaoId) throws SQLException, EntityNotFoundException {

        Instituicao instituicao = dao.getById(instituicaoId);

        if(instituicao == null) {
            throw new EntityNotFoundException("Instituição com ID: " + instituicaoId + " não encontrada!");
        }

        return instituicao;
    }

    public List<Instituicao> getAll() throws SQLException {
        return dao.getAll();
    }

    public Instituicao getById(Long idEntity) throws SQLException {
        return dao.getById(idEntity);
    }

    public Instituicao insert(Instituicao novaInstituicao) throws SQLException {
        validarInstituicao(novaInstituicao);
        return dao.insert(novaInstituicao);
    }

    public Instituicao update(Long idEntity, Instituicao instituicaoParaAlterar) throws SQLException, EntityNotFoundException {
        validarInstituicao(instituicaoParaAlterar);

        if(instituicaoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID da instituicao a ser atualizado é obrigatório.");
        }

        return dao.update(idEntity, instituicaoParaAlterar);
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        dao.remove(id);
    }
}