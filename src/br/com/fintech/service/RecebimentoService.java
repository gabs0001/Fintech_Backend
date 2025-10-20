package br.com.fintech.service;

import br.com.fintech.dao.RecebimentoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Service
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

    public Recebimento insert(Recebimento novoRecebimento) throws SQLException, IllegalArgumentException {
        validarRecebimento(novoRecebimento);
        return super.insert(novoRecebimento);
    }

    public Recebimento update(Long userId, Recebimento recebimentoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarRecebimento(recebimentoParaAlterar);
        return super.update(userId, recebimentoParaAlterar);
    }
}