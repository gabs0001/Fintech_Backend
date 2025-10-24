package br.com.fintech.service;

import br.com.fintech.dao.InvestimentoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Investimento;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class InvestimentoService extends CrudService<Investimento, Long> {
    private final TipoInvestimentoService tipoInvestimentoService;
    private final InstituicaoService instituicaoService;

    public InvestimentoService(InvestimentoDAO investimentoDAO, TipoInvestimentoService tipoInvestimentoService, InstituicaoService instituicaoService) {
        super(investimentoDAO);
        this.tipoInvestimentoService = tipoInvestimentoService;
        this.instituicaoService = instituicaoService;
    }

    private void validarInvestimento(Investimento investimento) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        if(!investimento.validarValor()) {
            throw new IllegalArgumentException("Erro: o valor do investimento deve ser maior que zero!");
        }

        if(investimento.getDataRealizacao() == null || investimento.getDataRealizacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data de realização do investimento não pode ser uma data futura!");
        }

        if(investimento.getDataVencimento() != null && !investimento.getDataVencimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data de vencimento, se fornecida, deve ser uma data futura!");
        }

        if(investimento.getDescricao() == null || investimento.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição do investimento é obrigatória e não pode estar em branco!");
        }

        if(investimento.getNome() == null || investimento.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome do investimento é obrigatório!");
        }

        Long idCategoria = investimento.getTipoInvestimentoId();

        if(idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("Erro: O investimento deve estar vinculado a uma categoria válida!");
        }

        tipoInvestimentoService.fetchOrThrowException(idCategoria);

        Long idInstituicao = investimento.getInstituicaoId();
        if(idInstituicao == null || idInstituicao <= 0) {
            throw new IllegalArgumentException("Erro: O investimento deve estar vinculado a uma instituição válida!");
        }

        instituicaoService.fetchOrThrowException(idInstituicao);
    }

    public Investimento insert(Investimento novoInvestimento) throws SQLException, IllegalArgumentException {
        validarInvestimento(novoInvestimento);
        return super.insert(novoInvestimento);
    }

    public Investimento update(Long ownerId, Investimento investimentoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarInvestimento(investimentoParaAlterar);

        if(investimentoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID do investimento a ser atualizado é obrigatório.");
        }

        return super.update(ownerId, investimentoParaAlterar);
    }
}