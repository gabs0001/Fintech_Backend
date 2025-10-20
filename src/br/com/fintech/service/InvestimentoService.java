package br.com.fintech.service;

import br.com.fintech.dao.InvestimentoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Categoria;
import br.com.fintech.model.Instituicao;
import br.com.fintech.model.Investimento;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class InvestimentoService extends CrudService<Investimento, Long> {

    public InvestimentoService(InvestimentoDAO investimentoDAO) { super(investimentoDAO); }

    private void validarInvestimento(Investimento novoInvestimento) throws IllegalArgumentException {
        if(!novoInvestimento.validarValor()) {
            throw new IllegalArgumentException("Erro: o valor do investimento deve ser maior que zero!");
        }

        if(novoInvestimento.getDataRealizacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data de realização do investimento não pode ser uma data futura!");
        }

        if(novoInvestimento.getDataVencimento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data de vencimento do investimento deve ser uma data futura!");
        }

        Instituicao instituicao = novoInvestimento.getInstituicao();
        if(instituicao == null || instituicao.getId() == null || instituicao.getId() <= 0) {
            throw new IllegalArgumentException("Erro: O investimento deve estar vinculado a uma instituição válida!");
        }

        Categoria categoria = novoInvestimento.getCategoria();
        if(categoria == null || categoria.getId() == null || categoria.getId() <= 0) {
            throw new IllegalArgumentException("Erro: O investimento deve estar vinculado a uma categoria válida!");
        }

        if(novoInvestimento.getNome() == null || novoInvestimento.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome do investimento é obrigatório!");
        }
    }

    public Investimento insert(Investimento novoInvestimento) throws SQLException, IllegalArgumentException {
        validarInvestimento(novoInvestimento);
        return super.insert(novoInvestimento);
    }

    public Investimento update(Long userId, Investimento investimentoParaAlterar) throws SQLException, EntityNotFoundException {
        validarInvestimento(investimentoParaAlterar);
        return super.update(userId, investimentoParaAlterar);
    }
}