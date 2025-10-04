package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Gasto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GastoDAO implements CrudDAO<Gasto, Long> {
    private final Connection conexao;

    public GastoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(Gasto gasto) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "INSERT INTO T_SIF_GASTO (COD_GASTO,DES_GASTO,VAL_GASTO,DAT_GASTO,COD_USUARIO,COD_CATEGORIA_GASTO) " +
                        "VALUES (SEQ_SIF_GASTO.NEXTVAL, ?,?,?,?,?)", new String[]{"COD_GASTO"}
        )) {
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

        return new Gasto(id, codUsuario, descricao, codCategoria, valor, data.toLocalDate());
    }

    public List<Gasto> getAll() throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement("SELECT * FROM T_SIF_GASTO");
            ResultSet result = stm.executeQuery()
        ) {
            List<Gasto> gastos = new ArrayList<>();

            while (result.next()) {
                gastos.add(parseGasto(result));
            }

            return gastos;
        }
    }

    public Gasto getById(Long gastoId, Long userId) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "SELECT * FROM T_SIF_GASTO WHERE COD_GASTO = ? AND COD_USUARIO = ?"
        )) {
            stm.setLong(1, gastoId);
            stm.setLong(2, userId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseGasto(result);
            }
        }
    }

    public void update(Gasto gasto) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "UPDATE T_SIF_GASTO SET " +
                        "DES_GASTO = ?, VAL_GASTO = ?, DAT_GASTO = ? " +
                        "WHERE COD_GASTO = ? AND COD_USUARIO = ?"
        )) {
            stm.setString(1, gasto.getDescricao());
            stm.setBigDecimal(2, gasto.getValor());
            stm.setDate(3, Date.valueOf(gasto.getDataGasto()));
            stm.setLong(4, gasto.getId());
            stm.setLong(5, gasto.getUsuarioId());

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Gasto com ID " + gasto.getId() + " não foi encontrado para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement("DELETE FROM T_SIF_GASTO WHERE COD_GASTO = ?")) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Gasto não encontrado!");
        }
    }

    public void fecharConexao() throws SQLException {
        if(conexao != null) conexao.close();
    }
}