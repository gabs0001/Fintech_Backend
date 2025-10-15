package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Instituicao;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstituicaoDAO implements CrudDAO<Instituicao, Long>, AutoCloseable {
    private final Connection conexao;

    public InstituicaoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(Instituicao instituicao) throws SQLException {
        String sql = "INSERT INTO T_SIF_INSTITUICAO (COD_INSTITUICAO, NOM_INSTITUICAO) " + "VALUES (SEQ_SIF_INSTITUICAO.NEXTVAL, ?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{"COD_INSTITUICAO"})) {
            stm.setString(1, instituicao.getNome());

            stm.executeUpdate();

            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    instituicao.setId(novoId);
                }
            }
        }
    }

    public Instituicao parseInstituicao(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_INSTITUICAO");
        String nome = result.getString("NOM_INSTITUICAO");

        return new Instituicao(id, nome);
    }

    public List<Instituicao> getAll() throws SQLException {
        String sql = "SELECT * FROM T_SIF_INSTITUICAO";

        try(PreparedStatement stm = conexao.prepareStatement(sql);
            ResultSet result = stm.executeQuery()
        ) {
            List<Instituicao> instituicoes = new ArrayList<>();

            while(result.next()) {
                instituicoes.add(parseInstituicao(result));
            }

            return instituicoes;
        }
    }

    public Instituicao getById(Long entityId, Long userId) throws SQLException {
        String sql = "SELECT * FROM T_SIF_INSTITUICAO WHERE COD_INSTITUICAO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, entityId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseInstituicao(result);
            }
        }
    }

    public void update(Instituicao instituicao) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_INSTITUICAO SET NOM_INSTITUICAO = ? WHERE COD_INSTITUICAO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, instituicao.getNome());
            stm.setLong(2, instituicao.getId());

            int linhasAfetadas = stm.executeUpdate();
            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Instituição com ID " + instituicao.getId() + " não foi encontrada para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_INSTITUICAO WHERE COD_INSTITUICAO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Instituição não encontrada!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}