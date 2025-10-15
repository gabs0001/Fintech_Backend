package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Categoria;
import br.com.fintech.model.Gasto;

import java.io.Closeable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GastoDAO implements CrudDAO<Gasto, Long>, AutoCloseable {
    private final Connection conexao;

    public GastoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(Gasto gasto) throws SQLException {
        String sql = "INSERT INTO T_SIF_GASTO (COD_GASTO,DES_GASTO,VAL_GASTO,DAT_GASTO,COD_USUARIO,COD_CATEGORIA_GASTO) " +
                "VALUES (SEQ_SIF_GASTO.NEXTVAL, ?,?,?,?,?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{"COD_GASTO"})) {
            stm.setString(1, gasto.getDescricao());
            stm.setBigDecimal(2, gasto.getValor());
            stm.setDate(3, Date.valueOf(gasto.getDataGasto()));
            stm.setLong(4, gasto.getUsuarioId());
            stm.setLong(5, gasto.getCategoriaId());

            stm.executeUpdate();

            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    gasto.setId(novoId);
                }
            }
        }
    }

    private Gasto parseGasto(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_GASTO");
        String descricao = result.getString("DES_GASTO");
        BigDecimal valor = result.getBigDecimal("VAL_GASTO");
        Date data = result.getDate("DAT_GASTO");
        Long codUsuario = result.getLong("COD_USUARIO");
        Long codCategoria = result.getLong("COD_CATEGORIA_GASTO");

        Categoria categoria = new Categoria(codCategoria, null);

        return new Gasto(id, codUsuario, descricao, categoria, valor, data.toLocalDate());
    }

    public List<Gasto> getAll() throws SQLException {
        String sql = "SELECT * FROM T_SIF_GASTO";

        try(PreparedStatement stm = conexao.prepareStatement(sql); ResultSet result = stm.executeQuery()) {
            List<Gasto> gastos = new ArrayList<>();

            while(result.next()) {
                gastos.add(parseGasto(result));
            }

            return gastos;
        }
    }

    public Gasto getById(Long gastoId, Long userId) throws SQLException {
        String sql = "SELECT * FROM T_SIF_GASTO WHERE COD_GASTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, gastoId);
            stm.setLong(2, userId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseGasto(result);
            }
        }
    }

    public void update(Gasto gasto) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_GASTO SET DES_GASTO = ?, VAL_GASTO = ?, DAT_GASTO = ?, COD_CATEGORIA_GASTO = ? WHERE COD_GASTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, gasto.getDescricao());
            stm.setBigDecimal(2, gasto.getValor());
            stm.setDate(3, Date.valueOf(gasto.getDataGasto()));
            stm.setLong(4, gasto.getCategoriaId());
            stm.setLong(4, gasto.getId());
            stm.setLong(5, gasto.getUsuarioId());

            int linhasAfetadas = stm.executeUpdate();
            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Gasto com ID " + gasto.getId() + " não foi encontrado para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_GASTO WHERE COD_GASTO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Gasto não encontrado!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}