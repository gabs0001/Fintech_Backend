package br.com.fintech.service;

import br.com.fintech.dao.InstituicaoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Instituicao;

import java.sql.SQLException;

public class InstituicaoService extends CrudService<Instituicao, Long> {

    public InstituicaoService(InstituicaoDAO instituicaoDAO) {
        super(instituicaoDAO);
    }

    private void validarInstituicao(Instituicao instituicao) throws IllegalArgumentException {
        if(instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da instituição é obrigatório e não pode estar em branco!");
        }
    }

    public void insert(Instituicao novaInstituicao) throws SQLException, IllegalArgumentException {
        validarInstituicao(novaInstituicao);
        super.insert(novaInstituicao);
    }

    public void update(Instituicao instituicaoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarInstituicao(instituicaoParaAlterar);
        super.update(instituicaoParaAlterar);
    }
}