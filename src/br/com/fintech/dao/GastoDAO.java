package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.model.Gasto;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GastoDAO implements CrudDAO<Gasto, Long>, AutoCloseable {
    private final Connection conexao;

    public GastoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public BigDecimal calcularTotal(Long userId) throws SQLException {
        String sql = "SELECT SUM(VAL_GASTO) AS TOTAL FROM T_SIF_GASTO WHERE COD_USUARIO = ?";

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
        String sql = "SELECT SUM(VAL_GASTO) AS TOTAL FROM T_SIF_GASTO WHERE COD_USUARIO = ? AND DAT_GASTO BETWEEN ? AND ?";

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

    public Gasto getUltimo(Long userId) throws SQLException {
        String sql = "SELECT G.*, C.DES_CATEGORIA_GASTO FROM T_SIF_GASTO G " +
                "JOIN T_SIF_CATEGORIA_GASTO C ON G.COD_CATEGORIA_GASTO = C.COD_CATEGORIA_GASTO " +
                "WHERE G.COD_USUARIO = ? ORDER BY G.DAT_GASTO DESC FETCH FIRST 1 ROW ONLY";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);

            try(ResultSet result = stm.executeQuery()) {
                if (!result.next()) return null;
                return parseGasto(result);
            }
        }
    }

    public List<Gasto> getUltimos(Long userId, int limite) throws SQLException {
        String sql = "SELECT G.*, C.DES_CATEGORIA_GASTO FROM T_SIF_GASTO G " +
                "JOIN T_SIF_CATEGORIA_GASTO C ON G.COD_CATEGORIA_GASTO = C.COD_CATEGORIA_GASTO " +
                "WHERE G.COD_USUARIO = ? ORDER BY G.DAT_GASTO DESC FETCH NEXT ? ROWS ONLY";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);
            stm.setInt(2, limite);

            List<Gasto> gastos = new ArrayList<>();

            try(ResultSet result = stm.executeQuery()) {
                while (result.next()) {
                    gastos.add(parseGasto(result));
                }

                return gastos;
            }
        }
    }

    public Gasto insert(Gasto gasto) throws SQLException {
        String sql = "INSERT INTO T_SIF_GASTO (COD_GASTO,DES_GASTO,VAL_GASTO,DAT_GASTO,COD_USUARIO,COD_CATEGORIA_GASTO) " +
                "VALUES (SEQ_SIF_GASTO.NEXTVAL, ?,?,?,?,?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{"COD_GASTO"})) {
            stm.setString(1, gasto.getDescricao());
            stm.setBigDecimal(2, gasto.getValor());
            stm.setDate(3, Date.valueOf(gasto.getDataGasto()));
            stm.setLong(4, gasto.getUsuarioId());
            stm.setLong(5, gasto.getCategoriaGastoId());

            stm.executeUpdate();

            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    gasto.setId(novoId);
                } else {
                    throw new SQLException("Falha ao obter o ID gerado para o Gasto. Nenhuma chave retornada");
                }
            }

            return gasto;
        }
    }

    private Gasto parseGasto(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_GASTO");
        String descricao = result.getString("DES_GASTO");
        BigDecimal valor = result.getBigDecimal("VAL_GASTO");
        Date data = result.getDate("DAT_GASTO");
        Long idUsuario = result.getLong("COD_USUARIO");
        Long codCategoria = result.getLong("COD_CATEGORIA_GASTO");

        CategoriaGasto categoriaGasto = new CategoriaGasto(codCategoria, null);

        return new Gasto(id, idUsuario, descricao, categoriaGasto, valor, data.toLocalDate());
    }

    public List<Gasto> getAllByUserId(Long ownerId) throws SQLException {
        String sql = "SELECT G.*, C.DES_CATEGORIA_GASTO FROM T_SIF_GASTO G " +
                "JOIN T_SIF_CATEGORIA_GASTO C ON G.COD_CATEGORIA_GASTO = C.COD_CATEGORIA_GASTO " +
                "WHERE G.COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, ownerId);

            List<Gasto> gastos = new ArrayList<>();

            try(ResultSet result = stm.executeQuery()) {
                while (result.next()) {
                    gastos.add(parseGasto(result));
                }

                return gastos;
            }
        }
    }

    public Gasto getById(Long idEntity, Long ownerId) throws SQLException {
        String sql = "SELECT G.*, C.DES_CATEGORIA_GASTO FROM T_SIF_GASTO G " +
                "JOIN T_SIF_CATEGORIA_GASTO C ON G.COD_CATEGORIA_GASTO = C.COD_CATEGORIA_GASTO " +
                "WHERE G.COD_GASTO = ? AND G.COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(2, ownerId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseGasto(result);
            }
        }
    }

    public Gasto update(Long ownerId, Gasto gasto) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_GASTO SET DES_GASTO = ?, VAL_GASTO = ?, DAT_GASTO = ?, COD_CATEGORIA_GASTO = ? WHERE COD_GASTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, gasto.getDescricao());
            stm.setBigDecimal(2, gasto.getValor());
            stm.setDate(3, Date.valueOf(gasto.getDataGasto()));
            stm.setLong(4, gasto.getCategoriaGastoId());
            stm.setLong(5, gasto.getId());
            stm.setLong(6, ownerId);

            int linhasAfetadas = stm.executeUpdate();
            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Gasto com ID " + gasto.getId() + " não foi encontrado para atualização!");
            }

            return gasto;
        }
    }

    public void remove(Long idEntity, Long ownerId) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_GASTO WHERE COD_GASTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(2, ownerId);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Gasto não encontrado!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}