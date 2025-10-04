package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Recebimento;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecebimentoDAO implements CrudDAO<Recebimento, Long> {
    private final Connection conexao;

    public RecebimentoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(Recebimento recebimento) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "INSERT INTO T_SIF_RECEBIMENTO (" +
                        "COD_RECEBIMENTO, VAL_RECEBIMENTO, DES_RECEBIMENTO, DAT_RECEBIMENTO, COD_USUARIO, COD_TIPO_RECEBIMENTO) " +
                        "VALUES (SEQ_SIF_RECEBIMENTO.NEXTVAL, ?,?,?,?,?)", new String[]{"COD_RECEBIMENTO"}
        )) {
            stm.setBigDecimal(1, recebimento.getValor());
            stm.setString(2, recebimento.getDescricao());
            stm.setDate(3, Date.valueOf(recebimento.getDataRecebimento()));
            stm.setLong(4, recebimento.getUsuarioId());
            stm.setLong(5, recebimento.getCategoriaId());

            stm.executeUpdate();

            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    recebimento.setId(novoId);
                }
            }
        }
    }

    private Recebimento parseRecebimento(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_RECEBIMENTO");
        BigDecimal valor = result.getBigDecimal("VAL_RECEBIMENTO");
        String descricao = result.getString("DES_RECEBIMENTO");
        Date dataRecebimento = result.getDate("DAT_RECEBIMENTO");
        Long codUsuario  = result.getLong("COD_USUARIO");
        Long codTipoRecebimento = result.getLong("COD_TIPO_RECEBIMENTO");

        return new Recebimento(id, codUsuario, descricao, codTipoRecebimento, valor, dataRecebimento.toLocalDate());
    }

    public List<Recebimento> getAll() throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement("SELECT * FROM T_SIF_RECEBIMENTO");
            ResultSet result = stm.executeQuery()) {

            List<Recebimento> recebimentos = new ArrayList<>();

            while (result.next()) {
                recebimentos.add(parseRecebimento(result));
            }

            return recebimentos;
        }
    }

    public Recebimento getById(Long idEntity, Long idUser) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "SELECT * FROM T_SIF_RECEBIMENTO WHERE COD_RECEBIMENTO = ? AND COD_USUARIO = ?"
        )) {
            stm.setLong(1, idEntity);
            stm.setLong(2, idUser);

            try(ResultSet result = stm.executeQuery()) {
                if (!result.next()) return null;
                return parseRecebimento(result);
            }
        }
    }

    public void update(Recebimento recebimento) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "UPDATE T_SIF_RECEBIMENTO SET " +
                        "VAL_RECEBIMENTO = ?, DES_RECEBIMENTO = ?, DAT_RECEBIMENTO = ? " +
                    "WHERE COD_RECEBIMENTO = ? AND COD_USUARIO = ?"
        )) {
            stm.setBigDecimal(1, recebimento.getValor());
            stm.setString(2, recebimento.getDescricao());
            stm.setDate(3, Date.valueOf(recebimento.getDataRecebimento()));
            stm.setLong(4, recebimento.getId());
            stm.setLong(5, recebimento.getUsuarioId());

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Recebimento com ID " + recebimento.getId() + " não foi encontrado para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement("DELETE FROM T_SIF_RECEBIMENTO WHERE COD_RECEBIMENTO = ?")) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Recebimento não encontrado!");
        }
    }

    public void fecharConexao() throws SQLException {
        if(conexao != null) conexao.close();
    }
}