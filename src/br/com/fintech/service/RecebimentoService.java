package br.com.fintech.service;

import br.com.fintech.dao.RecebimentoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;

import java.sql.SQLException;
import java.time.LocalDate;

public class RecebimentoService extends CrudService<Recebimento, Long>{

    public RecebimentoService(RecebimentoDAO recebimentoDAO) {
        super(recebimentoDAO);
    }

    private void validarRecebimento(Recebimento recebimento) throws IllegalArgumentException {
        if(!recebimento.validarValor()) {
            throw new IllegalArgumentException("Erro: O valor do recebimento deve ser maior que zero!");
        }

        if(recebimento.getDataRecebimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data do recebimento não pode ser uma data futura!");
        }
    }

    public void insert(Recebimento novoRecebimento) throws SQLException, IllegalArgumentException {
        validarRecebimento(novoRecebimento);
        super.insert(novoRecebimento);
    }

    public void update(Recebimento recebimentoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarRecebimento(recebimentoParaAlterar);
        super.update(recebimentoParaAlterar);
    }
}