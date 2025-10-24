package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Recebimento;
import br.com.fintech.model.TipoRecebimento;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecebimentoDAO implements CrudDAO<Recebimento, Long>, AutoCloseable {
    private final Connection conexao;

    public RecebimentoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public BigDecimal calcularTotal(Long userId) throws SQLException {
        String sql = "SELECT SUM(VAL_RECEBIMENTO) AS TOTAL FROM T_SIF_RECEBIMENTO WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);

            try(ResultSet result = stm.executeQuery()) {
                if (result.next()) {
                    return result.getBigDecimal("TOTAL") != null ? result.getBigDecimal("TOTAL") : BigDecimal.ZERO;
                }
                return BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "SELECT SUM(VAL_RECEBIMENTO) AS TOTAL FROM T_SIF_RECEBIMENTO WHERE COD_USUARIO = ? AND DAT_RECEBIMENTO BETWEEN ? AND ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);
            stm.setDate(2, Date.valueOf(inicio));
            stm.setDate(3, Date.valueOf(fim));

            try(ResultSet result = stm.executeQuery()) {
                if (result.next()) {
                    return result.getBigDecimal("TOTAL") != null ? result.getBigDecimal("TOTAL") : BigDecimal.ZERO;
                }
                return BigDecimal.ZERO;
            }
        }
    }

    public Recebimento getUltimo(Long userId) throws SQLException {
        String sql = "SELECT R.*, T.DES_TIPO_RECEBIMENTO FROM T_SIF_RECEBIMENTO R " +
                "JOIN T_SIF_TIPO_RECEBIMENTO T ON R.COD_TIPO_RECEBIMENTO = T.COD_TIPO_RECEBIMENTO " +
                "WHERE R.COD_USUARIO = ? ORDER BY R.DAT_RECEBIMENTO DESC FETCH FIRST 1 ROW ONLY";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);

            try(ResultSet result = stm.executeQuery()) {
                if (!result.next()) return null;
                return parseRecebimento(result);
            }
        }
    }

    public List<Recebimento> getUltimos(Long userId, int limite) throws SQLException {
        String sql = "SELECT R.*, T.DES_TIPO_RECEBIMENTO FROM T_SIF_RECEBIMENTO R " +
                "JOIN T_SIF_TIPO_RECEBIMENTO T ON R.COD_TIPO_RECEBIMENTO = T.COD_TIPO_RECEBIMENTO " +
                "WHERE R.COD_USUARIO = ? ORDER BY R.DAT_RECEBIMENTO DESC FETCH NEXT ? ROWS ONLY";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);
            stm.setInt(2, limite);

            List<Recebimento> recebimentos = new ArrayList<>();

            try(ResultSet result = stm.executeQuery()) {
                while (result.next()) {
                    recebimentos.add(parseRecebimento(result));
                }

                return recebimentos;
            }
        }
    }

    public Recebimento insert(Recebimento recebimento) throws SQLException {
        String sql = "INSERT INTO T_SIF_RECEBIMENTO (" +
                "COD_RECEBIMENTO, VAL_RECEBIMENTO, DES_RECEBIMENTO, DAT_RECEBIMENTO, COD_USUARIO, COD_TIPO_RECEBIMENTO) " +
                "VALUES (SEQ_SIF_RECEBIMENTO.NEXTVAL, ?,?,?,?,?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{"COD_RECEBIMENTO"})) {
            stm.setBigDecimal(1, recebimento.getValor());
            stm.setString(2, recebimento.getDescricao());
            stm.setDate(3, Date.valueOf(recebimento.getDataRecebimento()));
            stm.setLong(4, recebimento.getUsuarioId());
            stm.setLong(5, recebimento.getTipoRecebimentoId());

            stm.executeUpdate();

            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    recebimento.setId(novoId);
                } else {
                    throw new SQLException("Falha ao obter o ID gerado para o Recebimento. Nenhuma chave retornada");
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
        Long idUsuario  = result.getLong("COD_USUARIO");
        Long codTipoRecebimento = result.getLong("COD_TIPO_RECEBIMENTO");

        TipoRecebimento tipoRecebimento = new TipoRecebimento(codTipoRecebimento, null);

        return new Recebimento(id, idUsuario, descricao, tipoRecebimento, valor, dataRecebimento.toLocalDate());
    }

    public List<Recebimento> getAllByUserId(Long ownerId) throws SQLException {
        String sql = "SELECT R.*, T.DES_TIPO_RECEBIMENTO FROM T_SIF_RECEBIMENTO R " +
                "JOIN T_SIF_TIPO_RECEBIMENTO T ON R.COD_TIPO_RECEBIMENTO = T.COD_TIPO_RECEBIMENTO " +
                "WHERE R.COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, ownerId);

            try(ResultSet result = stm.executeQuery()) {

                List<Recebimento> recebimentos = new ArrayList<>();

                while (result.next()) {
                    recebimentos.add(parseRecebimento(result));
                }

                return recebimentos;
            }
        }
    }

    public Recebimento getById(Long idEntity, Long ownerId) throws SQLException {
        String sql = "SELECT R.*, T.DES_TIPO_RECEBIMENTO FROM T_SIF_RECEBIMENTO R " +
                "JOIN T_SIF_TIPO_RECEBIMENTO T ON R.COD_TIPO_RECEBIMENTO = T.COD_TIPO_RECEBIMENTO " +
                "WHERE R.COD_RECEBIMENTO = ? AND R.COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(2, ownerId);

            try(ResultSet result = stm.executeQuery()) {
                if (!result.next()) return null;
                return parseRecebimento(result);
            }
        }
    }

    public Recebimento update(Long ownerId, Recebimento recebimento) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_RECEBIMENTO SET " + "VAL_RECEBIMENTO = ?, DES_RECEBIMENTO = ?, DAT_RECEBIMENTO = ?, COD_TIPO_RECEBIMENTO = ? " +
                "WHERE COD_RECEBIMENTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setBigDecimal(1, recebimento.getValor());
            stm.setString(2, recebimento.getDescricao());
            stm.setDate(3, Date.valueOf(recebimento.getDataRecebimento()));
            stm.setLong(4, recebimento.getTipoRecebimentoId());
            stm.setLong(5, recebimento.getId());
            stm.setLong(6, ownerId);

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Recebimento com ID " + recebimento.getId() + " não foi encontrado para atualização!");
            }

            return recebimento;
        }
    }

    public void remove(Long idEntity, Long ownerId) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_RECEBIMENTO WHERE COD_RECEBIMENTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(2, ownerId);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Recebimento não encontrado!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}