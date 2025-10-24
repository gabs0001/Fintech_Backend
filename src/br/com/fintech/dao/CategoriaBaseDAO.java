package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.model.TipoInvestimento;
import br.com.fintech.model.TipoRecebimento;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public abstract class CategoriaBaseDAO implements AutoCloseable {
    protected String nomeTabela;
    protected String nomePk;
    protected String nomeSequencia;
    protected String descricaoColuna;
    protected Connection conexao;

    public CategoriaBaseDAO(String nomeTabela, String nomePK, String nomeSequencia, String descricaoColuna) throws SQLException {
        this.nomeTabela = nomeTabela;
        this.nomePk = nomePK;
        this.nomeSequencia = nomeSequencia;
        this.descricaoColuna = descricaoColuna;
        this.conexao = ConnectionFactory.getConnection();
    }

    protected abstract <T> T parse(ResultSet result) throws SQLException;

    public <T> T insert(T categoria) throws SQLException {
        Long novoId;

        String sql = "INSERT INTO " + this.nomeTabela + "(" + this.nomePk + ", " + this.descricaoColuna
                + ") VALUES (" + this.nomeSequencia + ".NEXTVAL, ?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{ this.nomePk })) {
            String descricao = "";

            if(categoria instanceof CategoriaGasto) {
                descricao = ((CategoriaGasto) categoria).getDescricao();
            }
            else if(categoria instanceof TipoRecebimento) {
                descricao = ((TipoRecebimento) categoria).getDescricao();
            }
            else if(categoria instanceof TipoInvestimento) {
                descricao = ((TipoInvestimento) categoria).getDescricao();
            }

            stm.setString(1, descricao);

            stm.executeUpdate();

            try(ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    novoId = generatedKeys.getLong(1);
                    if (categoria instanceof CategoriaGasto) {
                        ((CategoriaGasto) categoria).setId(novoId);
                    }
                    else if (categoria instanceof TipoRecebimento) {
                        ((TipoRecebimento) categoria).setId(novoId);
                    }
                    else if(categoria instanceof TipoInvestimento) {
                        ((TipoInvestimento) categoria).setId(novoId);
                    }
                } else {
                    throw new SQLException("Falha ao obter o ID gerado para a categoria. Nenhuma chave retornada.");
                }
            }

            return categoria;
        }
    }

    public List<?> getAll() throws SQLException {
        String sql = "SELECT * FROM " + this.nomeTabela;

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {

            List<Object> categorias = new ArrayList<>();

            try(ResultSet result = stm.executeQuery()) {
                while (result.next()) {
                    categorias.add(parse(result));
                }
                return categorias;
            }
        }
    }

    public Object getById(Long entityId) throws SQLException {
        String sql = "SELECT * FROM " + this.nomeTabela + " WHERE " + this.nomePk + " = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, entityId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parse(result);
            }
        }
    }

    public <T> T update(Long idEntity, T categoria) throws SQLException, EntityNotFoundException {
        String descricao = "";

        if(categoria instanceof CategoriaGasto) {
            descricao = ((CategoriaGasto) categoria).getDescricao();
        }
        else if (categoria instanceof TipoRecebimento) {
            descricao = ((TipoRecebimento) categoria).getDescricao();
        }
        else if(categoria instanceof TipoInvestimento) {
            descricao = ((TipoInvestimento) categoria).getDescricao();
        }

        String sql = "UPDATE " + this.nomeTabela + " SET " + this.descricaoColuna + " = ? WHERE "
                + this.nomePk + " = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, descricao);
            stm.setLong(2, idEntity);

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                Long id = null;

                if(categoria instanceof CategoriaGasto) {
                    id = ((CategoriaGasto) categoria).getId();
                }
                else if (categoria instanceof TipoRecebimento) {
                    id = ((TipoRecebimento) categoria).getId();
                }
                else if(categoria instanceof TipoInvestimento) {
                    id = ((TipoInvestimento) categoria).getId();
                }

                throw new EntityNotFoundException("Erro: Categoria com ID " + id
                        + " não foi encontrada para atualização!");
            }

            return categoria;
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM " + this.nomeTabela + " WHERE " + this.nomePk + " = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Categoria não encontrada!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}