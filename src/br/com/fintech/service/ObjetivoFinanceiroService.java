package br.com.fintech.service;

import br.com.fintech.dao.ObjetivoFinanceiroDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.ObjetivoFinanceiro;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class ObjetivoFinanceiroService extends CrudService<ObjetivoFinanceiro, Long>{

    public ObjetivoFinanceiroService(ObjetivoFinanceiroDAO objetivoFinanceiroDAO) {
        super(objetivoFinanceiroDAO);
    }

    private void validarObjetivo(ObjetivoFinanceiro objetivo) throws EntityNotFoundException, IllegalArgumentException {
        if(objetivo.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Erro: O valor do objetivo deve ser maior que zero!");
        }

        if(objetivo.getDataConclusao() == null || objetivo.getDataConclusao().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data de conclusão do objetivo deve ser uma data futura!");
        }

        if(objetivo.getDescricao() == null || objetivo.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição do objetivo é obrigatória e não pode estar em branco!");
        }
    }

    public ObjetivoFinanceiro insert(ObjetivoFinanceiro novoObjetivo) throws SQLException, IllegalArgumentException {
        validarObjetivo(novoObjetivo);
        return super.insert(novoObjetivo);
    }

    public ObjetivoFinanceiro update(Long ownerId, ObjetivoFinanceiro objetivoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarObjetivo(objetivoParaAlterar);

        if(objetivoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID do objetivo a ser atualizado é obrigatório.");
        }

        return super.update(ownerId, objetivoParaAlterar);
    }
}