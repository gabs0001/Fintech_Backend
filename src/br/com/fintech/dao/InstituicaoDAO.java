package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Instituicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstituicaoDAO implements CrudDAO<Instituicao, Long> {
    private final Connection conexao;

    public InstituicaoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(Instituicao instituicao) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement("INSERT INTO T_SIF_INSTITUICAO (COD_INSTITUICAO, NOM_INSTITUICAO) " +
                "VALUES (SEQ_SIF_INSTITUICAO.NEXTVAL, ?)", new String[]{"COD_INSTITUICAO"}
        )) {
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
        try(PreparedStatement stm = conexao.prepareStatement("SELECT * FROM T_SIF_INSTITUICAO");
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
        try(PreparedStatement stm = conexao.prepareStatement(
                "SELECT * FROM T_SIF_INSTITUICAO WHERE COD_INSTITUICAO = ?"
        )) {
            stm.setLong(1, entityId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseInstituicao(result);
            }
        }
    }

    public void update(Instituicao instituicao) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "UPDATE T_SIF_INSTITUICAO SET NOM_INSTITUICAO = ? WHERE COD_INSTITUICAO = ?"
        )) {
            stm.setString(1, instituicao.getNome());
            stm.setLong(2, instituicao.getId());

            int linhasAfetadas = stm.executeUpdate();
            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Gasto com ID " + instituicao.getId() + " não foi encontrado para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement("DELETE FROM T_SIF_INSTITUICAO WHERE COD_INSTITUICAO = ?")) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Instituição não encontrada!");
        }
    }

    public void fecharConexao() throws SQLException {
        if(conexao != null) conexao.close();
    }
}