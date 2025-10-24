package br.com.fintech.service;

import br.com.fintech.dao.TipoRecebimentoDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.TipoRecebimento;

import java.sql.SQLException;

public class TipoRecebimentoService extends CategoriaBaseService<TipoRecebimento, TipoRecebimentoDAO> {

    public TipoRecebimentoService(TipoRecebimentoDAO dao) {
        super(dao);
    }

    @Override
    protected void validar(TipoRecebimento categoria) throws IllegalArgumentException {
        if(categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição da categoria de Recebimento é obrigatória e não pode estar em branco!");
        }
    }

    @Override
    protected TipoRecebimento fetchOrThrowException(Long categoriaId) throws SQLException, EntityNotFoundException {
        TipoRecebimento categoria = getById(categoriaId);

        if(categoria == null) {
            throw new EntityNotFoundException("Erro: Categoria de Recebimento com ID " + categoriaId + " não encontrada.");
        }
        return categoria;
    }
}