package br.com.fintech.dao;

import br.com.fintech.model.CategoriaGasto;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CategoriaGastoDAO extends CategoriaBaseDAO {

    public CategoriaGastoDAO() throws SQLException {
        super(
                "T_SIF_CATEGORIA_GASTO",
                "COD_CATEGORIA_GASTO",
                "SEQ_SIF_CATEGORIA_GASTO",
                "DES_CATEGORIA_GASTO"
        );
    }

    @Override
    public CategoriaGasto parse(ResultSet result) throws SQLException {
        Long id = result.getLong(this.nomePk);
        String descricao = result.getString(this.descricaoColuna);

        return new CategoriaGasto(id, descricao);
    }
}