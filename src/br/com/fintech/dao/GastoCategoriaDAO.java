package br.com.fintech.dao;

import java.sql.SQLException;

public class GastoCategoriaDAO extends CategoriaBaseDAO {

    public GastoCategoriaDAO() throws SQLException {
        super(
                "T_SIF_CATEGORIA_GASTO",
                "COD_CATEGORIA_GASTO",
                "SEQ_SIF_CATEGORIA_GASTO",
                "DES_CATEGORIA_GASTO"
        );
    }
}