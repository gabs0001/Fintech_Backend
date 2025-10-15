package br.com.fintech.dao;

import java.sql.SQLException;

public class RecebimentoCategoriaDAO extends CategoriaBaseDAO {

    public RecebimentoCategoriaDAO() throws SQLException {
        super(
                "T_SIF_TIPO_RECEBIMENTO",
                "COD_TIPO_RECEBIMENTO",
                "SEQ_SIF_TIPO_RECEBIMENTO",
                "DES_TIPO_RECEBIMENTO"
        );
    }
}