package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Categoria;
import br.com.fintech.model.Recebimento;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecebimentoDAO implements CrudDAO<Recebimento, Long>, AutoCloseable {
    private final Connection conexao;

    public RecebimentoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public Recebimento insert(Recebimento recebimento) throws SQLException {
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

            return recebimento;
        }
    }

    private Recebimento parseRecebimento(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_RECEBIMENTO");
        BigDecimal valor = result.getBigDecimal("VAL_RECEBIMENTO");
        String descricao = result.getString("DES_RECEBIMENTO");
        Date dataRecebimento = result.getDate("DAT_RECEBIMENTO");
        Long codUsuario  = result.getLong("COD_USUARIO");
        Long codTipoRecebimento = result.getLong("COD_TIPO_RECEBIMENTO");

        Categoria categoria = new Categoria(codTipoRecebimento, null);

        return new Recebimento(id, codUsuario, descricao, categoria, valor, dataRecebimento.toLocalDate());
    }

    public List<Recebimento> getAllByUserId(Long userId) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement("SELECT * FROM T_SIF_RECEBIMENTO WHERE COD_USUARIO = ?")) {
            stm.setLong(1, userId);

            try(ResultSet result = stm.executeQuery()) {

                List<Recebimento> recebimentos = new ArrayList<>();

                while (result.next()) {
                    recebimentos.add(parseRecebimento(result));
                }

                return recebimentos;
            }
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

    public Recebimento update(Long userId, Recebimento recebimento) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "UPDATE T_SIF_RECEBIMENTO SET " +
                        "VAL_RECEBIMENTO = ?, DES_RECEBIMENTO = ?, DAT_RECEBIMENTO = ?, COD_TIPO_RECEBIMENTO = ? " +
                    "WHERE COD_RECEBIMENTO = ? AND COD_USUARIO = ?"
        )) {
            stm.setBigDecimal(1, recebimento.getValor());
            stm.setString(2, recebimento.getDescricao());
            stm.setDate(3, Date.valueOf(recebimento.getDataRecebimento()));
            stm.setLong(4, recebimento.getCategoriaId());
            stm.setLong(5, recebimento.getId());
            stm.setLong(6, userId);

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Recebimento com ID " + recebimento.getId() + " não foi encontrado para atualização!");
            }

            return recebimento;
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement("DELETE FROM T_SIF_RECEBIMENTO WHERE COD_RECEBIMENTO = ?")) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Recebimento não encontrado!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}