package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.ObjetivoFinanceiro;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ObjetivoFinanceiroDAO implements CrudDAO<ObjetivoFinanceiro, Long>, AutoCloseable {
    private final Connection conexao;

    public ObjetivoFinanceiroDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(ObjetivoFinanceiro objetivoFinanceiro) throws SQLException {
        String sql = "INSERT INTO T_SIF_OBJETIVO_FINANCEIRO (COD_OBJETIVO, NOM_OBJETIVO, DES_OBJETIVO, VAL_OBJETIVO, DAT_CONCLUSAO_OBJETIVO, COD_USUARIO) " +
                "VALUES (SEQ_SIF_OBJETIVO_FINANCEIRO.NEXTVAL, ?,?,?,?,?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{"COD_OBJETIVO"})) {
            stm.setString(1, objetivoFinanceiro.getNome());
            stm.setString(2, objetivoFinanceiro.getDescricao());
            stm.setBigDecimal(3, objetivoFinanceiro.getValor());
            stm.setDate(4, Date.valueOf(objetivoFinanceiro.getDataConclusao()));
            stm.setLong(5, objetivoFinanceiro.getUsuarioId());

            stm.executeUpdate();

            try(ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    objetivoFinanceiro.setId(novoId);
                }
            }
        }
    }

    public ObjetivoFinanceiro parseObjetivo(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_OBJETIVO");
        String nome = result.getString("NOM_OBJETIVO");
        String descricao = result.getString("DES_OBJETIVO");
        BigDecimal valor = result.getBigDecimal("VAL_OBJETIVO");
        Date dataConclusao = result.getDate("DAT_CONCLUSAO_OBJETIVO");
        Long idUsuario = result.getLong("COD_USUARIO");

        LocalDate novaDataConclusao = dataConclusao != null ? dataConclusao.toLocalDate() : null;

        return new ObjetivoFinanceiro(id, idUsuario, nome, descricao, valor, novaDataConclusao);
    }

    public List<ObjetivoFinanceiro> getAll() throws SQLException {
        String sql = "SELECT * FROM T_SIF_OBJETIVO_FINANCEIRO";

        try(PreparedStatement stm = conexao.prepareStatement(sql);
            ResultSet result = stm.executeQuery()) {

            List<ObjetivoFinanceiro> objetivosFinanceiros = new ArrayList<>();

            while(result.next()) {
                objetivosFinanceiros.add(parseObjetivo(result));
            }

            return objetivosFinanceiros;
        }
    }

    public ObjetivoFinanceiro getById(Long idEntity, Long idUser) throws SQLException {
        String sql = "SELECT * FROM T_SIF_OBJETIVO_FINANCEIRO WHERE COD_OBJETIVO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(1, idUser);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseObjetivo(result);
            }
        }
    }

    public void update(ObjetivoFinanceiro objetivoFinanceiro) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_OBJETIVO_FINANCEIRO SET NOM_OBJETIVO = ?, DES_OBJETIVO = ?, VAL_OBJETIVO = ?, DAT_CONCLUSAO = ? " +
                "WHERE COD_OBJETIVO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, objetivoFinanceiro.getNome());
            stm.setString(2, objetivoFinanceiro.getDescricao());
            stm.setBigDecimal(3, objetivoFinanceiro.getValor());
            stm.setDate(4, Date.valueOf(objetivoFinanceiro.getDataConclusao()));
            stm.setLong(5, objetivoFinanceiro.getId());
            stm.setLong(6, objetivoFinanceiro.getUsuarioId());

            int linhasAfetadas = stm.executeUpdate();
            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Objetivo Financeiro com ID " + objetivoFinanceiro.getId() +
                        " não foi encontrado para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_OBJETIVO_FINANCEIRO WHERE COD_OBJETIVO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Objetivo Financeiro não encontrado!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}