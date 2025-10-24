package br.com.fintech.service;

import br.com.fintech.dao.RecebimentoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Recebimento;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class RecebimentoService extends CrudService<Recebimento, Long> {
    private final TipoRecebimentoService tipoRecebimentoService;

    public RecebimentoService(RecebimentoDAO recebimentoDAO, TipoRecebimentoService tipoRecebimentoService) {
        super(recebimentoDAO);
        this.tipoRecebimentoService = tipoRecebimentoService;
    }

    private void validarRecebimento(Recebimento recebimento) throws SQLException, EntityNotFoundException, IllegalArgumentException {
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

    public Recebimento insert(Recebimento novoRecebimento) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarRecebimento(novoRecebimento);
        return super.insert(novoRecebimento);
    }

    public Recebimento update(Long ownerId, Recebimento recebimentoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarRecebimento(recebimentoParaAlterar);

        if(recebimentoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID do recebimento a ser atualizado é obrigatório.");
        }

        return super.update(ownerId, recebimentoParaAlterar);
    }
}