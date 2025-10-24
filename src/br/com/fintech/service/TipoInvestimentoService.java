package br.com.fintech.service;

import br.com.fintech.dao.TipoInvestimentoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.TipoInvestimento;

import java.sql.SQLException;

public class TipoInvestimentoService extends CategoriaBaseService<TipoInvestimento, TipoInvestimentoDAO> {

    public TipoInvestimentoService(TipoInvestimentoDAO dao) {
        super(dao);
    }

    @Override
    protected void validar(TipoInvestimento categoria) throws IllegalArgumentException {
        if(categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição da categoria de Investimento é obrigatória e não pode estar em branco!");
        }
    }

    @Override
    protected TipoInvestimento fetchOrThrowException(Long categoriaId) throws SQLException, EntityNotFoundException {
        TipoInvestimento categoria = getById(categoriaId);

        if(categoria == null) {
            throw new EntityNotFoundException("Erro: Categoria de Investimento com ID " + categoriaId + " não encontrada.");
        }
        return categoria;
    }
}
