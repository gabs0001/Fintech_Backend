package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;
import br.com.fintech.repository.RecebimentoRepository; // Nova dependência JPA
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecebimentoService extends CrudService<Recebimento, Long> {
    private final TipoRecebimentoService tipoRecebimentoService;
    private final RecebimentoRepository recebimentoRepository;

    public RecebimentoService(RecebimentoRepository recebimentoRepository, TipoRecebimentoService tipoRecebimentoService) {
        super(recebimentoRepository);
        this.recebimentoRepository = recebimentoRepository;
        this.tipoRecebimentoService = tipoRecebimentoService;
    }

    private void validarRecebimento(Recebimento recebimento) throws EntityNotFoundException, IllegalArgumentException {
        if(!recebimento.validarValor()) {
            throw new IllegalArgumentException("Erro: O valor do recebimento deve ser maior que zero!");
        }

        if(recebimento.getDataRecebimento() == null || recebimento.getDataRecebimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data do recebimento não pode ser uma data futura!");
        }

        if(recebimento.getDescricao() == null || recebimento.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição do recebimento é obrigatória e não pode estar em branco!");
        }

        if(recebimento.getTipoRecebimentoId() == null) {
            throw new IllegalArgumentException("Erro: A categoria do recebimento é obrigatória!");
        }

        tipoRecebimentoService.fetchOrThrowException(recebimento.getTipoRecebimentoId());
    }

    public Recebimento insert(Recebimento novoRecebimento) throws EntityNotFoundException, IllegalArgumentException {
        validarRecebimento(novoRecebimento);
        return super.save(novoRecebimento);
    }

    public Recebimento update(Long ownerId, Recebimento recebimentoParaAlterar) throws EntityNotFoundException, IllegalArgumentException {
        validarRecebimento(recebimentoParaAlterar);

        if(recebimentoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID do recebimento a ser atualizado é obrigatório.");
        }

        super.fetchOrThrowExceptionByOwner(recebimentoParaAlterar.getId(), ownerId);

        return super.save(recebimentoParaAlterar);
    }

    public Recebimento getById(Long idEntity, Long ownerId) throws EntityNotFoundException {
        return super.fetchOrThrowExceptionByOwner(idEntity, ownerId);
    }

    public void remove(Long idEntity, Long ownerId) throws EntityNotFoundException {
        super.deleteByIdAndOwnerId(idEntity, ownerId);
    }

    public List<Recebimento> getUltimos(Long userId, int limite) {
        return recebimentoRepository.findTopNByUsuarioIdOrderByDataRecebimentoDesc(userId, limite);
    }

    public BigDecimal calcularTotal(Long userId) {
        return recebimentoRepository.calcularTotal(userId);
    }

    public BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim) {
        return recebimentoRepository.calcularTotalPeriodo(userId, inicio, fim);
    }
}