package br.com.fintech.dao;

import br.com.fintech.model.TipoInvestimento;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class TipoInvestimentoDAO extends CategoriaBaseDAO {

    public TipoInvestimentoDAO() throws SQLException {
        super(
                "T_SIF_TIPO_INVESTIMENTO",
                "COD_TIPO_INVESTIMENTO",
                "SEQ_SIF_TIPO_INVESTIMENTO",
                "DES_TIPO_INVESTIMENTO"
        );
    }

    @Override
    public TipoInvestimento parse(ResultSet result) throws SQLException {
        Long id = result.getLong(this.nomePk);
        String descricao = result.getString(this.descricaoColuna);

        return new TipoInvestimento(id, descricao);
    }
}