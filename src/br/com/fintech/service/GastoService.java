package br.com.fintech.service;

import br.com.fintech.dao.GastoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Gasto;

import java.sql.SQLException;
import java.time.LocalDate;

public class GastoService extends CrudService<Gasto, Long> {

    public GastoService(GastoDAO gastoDAO) {
        super(gastoDAO);
    }

    private void validarGasto(Gasto gasto) throws IllegalArgumentException {
        if(!gasto.validarValor()) {
            throw new IllegalArgumentException("Erro: O valor do gasto deve ser maior que zero!");
        }

        if(gasto.getDataGasto().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data do gasto não pode ser uma data futura!");
        }
    }

    public void insert(Gasto novoGasto) throws SQLException, IllegalArgumentException {
        validarGasto(novoGasto);
        super.insert(novoGasto);
    }

    public void update(Gasto gastoParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarGasto(gastoParaAlterar);
        super.update(gastoParaAlterar);
    }
}