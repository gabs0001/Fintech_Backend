package br.com.fintech.dao;

import java.sql.SQLException;

public class InvestimentoCategoriaDAO extends CategoriaBaseDAO {

    public InvestimentoCategoriaDAO() throws SQLException {
        super(
                "T_SIF_TIPO_INVESTIMENTO",
                "COD_TIPO_INVESTIMENTO",
                "SEQ_SIF_TIPO_INVESTIMENTO",
                "DES_TIPO_INVESTIMENTO"
        );
    }
}