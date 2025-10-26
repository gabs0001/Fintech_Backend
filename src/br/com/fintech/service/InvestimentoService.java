package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Investimento;
import br.com.fintech.repository.InvestimentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvestimentoService extends CrudService<Investimento, Long> {
    private final TipoInvestimentoService tipoInvestimentoService;
    private final InstituicaoService instituicaoService;
    private final InvestimentoRepository investimentoRepository;

    public InvestimentoService(
            InvestimentoRepository investimentoRepository,
            TipoInvestimentoService tipoInvestimentoService,
            InstituicaoService instituicaoService
    ) {
        super(investimentoRepository);
        this.tipoInvestimentoService = tipoInvestimentoService;
        this.investimentoRepository = investimentoRepository;
        this.instituicaoService = instituicaoService;
    }

    private void validarInvestimento(Investimento investimento) throws EntityNotFoundException, IllegalArgumentException {
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

        if (investimento.getTipoInvestimento() == null) {
            throw new IllegalArgumentException("Erro: O Tipo de Investimento é obrigatório!");
        }

        Long idCategoria = investimento.getTipoInvestimentoId();
        if(idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("Erro: O investimento deve estar vinculado a uma categoria válida!");
        }

        tipoInvestimentoService.getById(idCategoria);

        if (investimento.getInstituicao() == null) {
            throw new IllegalArgumentException("Erro: A Instituição é obrigatória!");
        }

        Long idInstituicao = investimento.getInstituicaoId();
        if(idInstituicao == null || idInstituicao <= 0) {
            throw new IllegalArgumentException("Erro: O investimento deve estar vinculado a uma instituição válida!");
        }

        instituicaoService.fetchOrThrowException(idInstituicao);
    }

    public Investimento insert(Investimento novoInvestimento) throws IllegalArgumentException, EntityNotFoundException {
        validarInvestimento(novoInvestimento);
        return super.save(novoInvestimento);
    }

    public Investimento update(Long ownerId, Investimento investimentoParaAlterar) throws EntityNotFoundException, IllegalArgumentException {
        validarInvestimento(investimentoParaAlterar);

        if(investimentoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID do investimento a ser atualizado é obrigatório.");
        }

        super.fetchOrThrowExceptionByOwner(investimentoParaAlterar.getId(), ownerId);

        return super.save(investimentoParaAlterar);
    }

    public void remove(Long idEntity, Long ownerId) throws EntityNotFoundException {
        super.deleteByIdAndOwnerId(idEntity, ownerId);
    }

    public Investimento getById(Long idEntity, Long ownerId) throws EntityNotFoundException {
        return super.fetchOrThrowExceptionByOwner(idEntity, ownerId);
    }

    public List<Investimento> getUltimos(Long userId, int limite) {
        return investimentoRepository.findTopNByUsuarioIdOrderByDataRealizacaoDesc(userId, limite);
    }

    public BigDecimal calcularTotal(Long userId) {
        return investimentoRepository.calcularTotal(userId);
    }

    public BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim) {
        return investimentoRepository.calcularTotalPeriodo(userId, inicio, fim);
    }
}