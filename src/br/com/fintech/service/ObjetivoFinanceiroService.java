package br.com.fintech.service;

import br.com.fintech.dao.ObjetivoFinanceiroDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.ObjetivoFinanceiro;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public class ObjetivoFinanceiroService extends CrudService<ObjetivoFinanceiro, Long>{

    public ObjetivoFinanceiroService(ObjetivoFinanceiroDAO objetivoFinanceiroDAO) {
        super(objetivoFinanceiroDAO);
    }

    private void validarObjetivo(ObjetivoFinanceiro objetivo) throws IllegalArgumentException {
        if(objetivo.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Erro: O valor do objetivo deve ser maior que zero!");
        }

        if(objetivo.getDataConclusao().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data de conclusão do objetivo deve ser uma data futura!");
        }
    }

    public void insert(ObjetivoFinanceiro novoObjetivo) throws SQLException, IllegalArgumentException {
        validarObjetivo(novoObjetivo);
        super.insert(novoObjetivo);
    }

    public void update(ObjetivoFinanceiro objetivoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarObjetivo(objetivoParaAlterar);
        super.update(objetivoParaAlterar);
    }
}