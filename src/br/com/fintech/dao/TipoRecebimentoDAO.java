package br.com.fintech.dao;

import br.com.fintech.model.TipoRecebimento;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class TipoRecebimentoDAO extends CategoriaBaseDAO {

    public TipoRecebimentoDAO() throws SQLException {
        super(
                "T_SIF_TIPO_RECEBIMENTO",
                "COD_TIPO_RECEBIMENTO",
                "SEQ_SIF_TIPO_RECEBIMENTO",
                "DES_TIPO_RECEBIMENTO"
        );
    }

    @Override
    public TipoRecebimento parse(ResultSet result) throws SQLException {
        Long id = result.getLong(this.nomePk);
        String descricao = result.getString(this.descricaoColuna);

        return new TipoRecebimento(id, descricao);
    }
}