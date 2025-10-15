package br.com.fintech.service;

import br.com.fintech.dao.CategoriaBaseDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Categoria;

import java.sql.SQLException;

public class CategoriaService extends CrudService<Categoria, Long> {

    public CategoriaService(CategoriaBaseDAO categoriaBaseDAO) {
        super(categoriaBaseDAO);
    }

    private void validarCategoria(Categoria categoria) throws IllegalArgumentException {
        if(categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da categoria é obrigatório e não pode estar em branco!");
        }
    }

    public void insert(Categoria novaCategoria) throws SQLException, IllegalArgumentException {
        validarCategoria(novaCategoria);
        super.insert(novaCategoria);
    }

    public void update(Categoria categoriaParaAlterar) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        validarCategoria(categoriaParaAlterar);
        super.update(categoriaParaAlterar);
    }
}