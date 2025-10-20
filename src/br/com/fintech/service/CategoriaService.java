package br.com.fintech.service;

import br.com.fintech.dao.CategoriaBaseDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Categoria;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaBaseDAO dao;

    public CategoriaService(CategoriaBaseDAO dao) {
        this.dao = dao;
    }

    private void validarCategoria(Categoria categoria) throws IllegalArgumentException {
        if(categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da categoria é obrigatório e não pode estar em branco!");
        }
    }

    public List<Categoria> getAll() throws SQLException {
        return dao.getAll();
    }

    public Categoria getById(Long entityId) throws SQLException {
        return dao.getById(entityId);
    }

    public Categoria insert(Categoria novaCategoria) throws SQLException {
        validarCategoria(novaCategoria);
        return dao.insert(novaCategoria);
    }

    public Categoria update(Long idEntity, Categoria categoriaParaAlterar) throws SQLException, EntityNotFoundException {
        validarCategoria(categoriaParaAlterar);
        return dao.update(idEntity, categoriaParaAlterar);
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        dao.remove(id);
    }
}