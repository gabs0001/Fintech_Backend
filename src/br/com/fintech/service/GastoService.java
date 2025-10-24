package br.com.fintech.service;

import br.com.fintech.dao.GastoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Gasto;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class GastoService extends CrudService<Gasto, Long> {
    private final CategoriaGastoService categoriaGastoService;

    public GastoService(GastoDAO gastoDAO, CategoriaGastoService categoriaGastoService) {
        super(gastoDAO);
        this.categoriaGastoService = categoriaGastoService;
    }

    private void validarGasto(Gasto gasto) throws IllegalArgumentException, SQLException, EntityNotFoundException {
        if(!gasto.validarValor()) {
            throw new IllegalArgumentException("Erro: O valor do gasto deve ser maior que zero!");
        }

        if(gasto.getDataGasto() == null || gasto.getDataGasto().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data do gasto não pode ser uma data futura!");
        }

        if(gasto.getDescricao() == null || gasto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição do gasto é obrigatória e não pode estar em branco!");
        }

        if(gasto.getCategoriaGastoId() == null) {
            throw new IllegalArgumentException("Erro: A Categoria do gasto é obrigatória!");
        }

        categoriaGastoService.fetchOrThrowException(gasto.getCategoriaGastoId());
    }

    public Gasto insert(Gasto novoGasto) throws SQLException, IllegalArgumentException {
        validarGasto(novoGasto);
        return super.insert(novoGasto);
    }

    public Gasto update(Long ownerId, Gasto gastoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarGasto(gastoParaAlterar);

        if(gastoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID do gasto a ser atualizado é obrigatório.");
        }

        return super.update(ownerId, gastoParaAlterar);
    }
}