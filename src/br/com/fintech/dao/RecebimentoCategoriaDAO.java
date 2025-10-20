package br.com.fintech.dao;

import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
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