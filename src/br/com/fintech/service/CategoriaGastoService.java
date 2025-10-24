package br.com.fintech.service;

import br.com.fintech.dao.CategoriaGastoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.CategoriaGasto;

import java.sql.SQLException;

public class CategoriaGastoService extends CategoriaBaseService<CategoriaGasto, CategoriaGastoDAO> {

    public CategoriaGastoService(CategoriaGastoDAO dao) {
        super(dao);
    }

    @Override
    protected void validar(CategoriaGasto categoria) throws IllegalArgumentException {
        if(categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição da Categoria de Gasto é obrigatória e não pode estar em branco!");
        }
    }

    @Override
    protected CategoriaGasto fetchOrThrowException(Long categoriaId) throws SQLException, EntityNotFoundException {
        CategoriaGasto categoria = getById(categoriaId);

        if(categoria == null) {
            throw new EntityNotFoundException("Erro: Categoria de Gasto com ID " + categoriaId + " não encontrada.");
        }
        return categoria;
    }
}
