package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Investimento;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvestimentoDAO implements CrudDAO<Investimento, Long> {
    private final Connection conexao;

    public InvestimentoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(Investimento investimento) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "INSERT INTO T_SIF_INVESTIMENTO (" +
                        "COD_INVESTIMENTO, NOM_APLICACAO, VAL_APLICACAO, DES_INVESTIMENTO, DAT_REALIZACAO, DAT_VENCIMENTO, " +
                        "COD_USUARIO, COD_TIPO_INVESTIMENTO, COD_INSTITUICAO) " +
                        "VALUES (SEQ_SIF_INVESTIMENTO.NEXTVAL,?,?,?,?,?,?,?,?)", new String[]{"COD_INVESTIMENTO"}
        )) {
            stm.setString(1, investimento.getNome());
            stm.setBigDecimal(2, investimento.getValor());
            stm.setString(3, investimento.getDescricao());
            stm.setDate(4, Date.valueOf(investimento.getDataRealizacao()));
            stm.setDate(5, Date.valueOf(investimento.getDataVencimento()));
            stm.setLong(6, investimento.getUsuarioId());
            stm.setLong(7, investimento.getCategoriaId());
            stm.setLong(8, investimento.getInstituicaoId());

            stm.executeUpdate();

            try(ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    investimento.setId(novoId);
                }
            }
        }
    }

    public Investimento parseInvestimento(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_INVESTIMENTO");
        String nome = result.getString("NOM_APLICACAO");
        BigDecimal valor = result.getBigDecimal("VAL_APLICACAO");
        String descricao = result.getString("DES_INVESTIMENTO");
        Date dataRealizacao = result.getDate("DAT_REALIZACAO");
        Date dataVencimento = result.getDate("DAT_VENCIMENTO");
        Long usuarioId = result.getLong("COD_USUARIO");
        Long categoriaId = result.getLong("COD_TIPO_INVESTIMENTO");
        Long instituicaoId = result.getLong("COD_INSTITUICAO");

        return new Investimento(
                id,
                usuarioId,
                descricao,
                categoriaId,
                valor,
                nome,
                dataRealizacao != null ? dataRealizacao.toLocalDate() : null,
                dataVencimento != null ? dataVencimento.toLocalDate() : null,
                instituicaoId
        );
    }

    public List<Investimento> getAll() throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement("SELECT * FROM T_SIF_INVESTIMENTO");
             ResultSet result = stm.executeQuery()) {

            List<Investimento> investimentos = new ArrayList<>();

            while(result.next()) {
                investimentos.add(parseInvestimento(result));
            }

            return investimentos;
        }
    }

    public Investimento getById(Long idEntity, Long idUser) throws SQLException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "SELECT * FROM T_SIF_INVESTIMENTO WHERE COD_INVESTIMENTO = ? AND COD_USUARIO = ?"
        )) {
            stm.setLong(1, idEntity);
            stm.setLong(2, idUser);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseInvestimento(result);
            }
        }
    }

    public void update(Investimento investimento) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement(
                "UPDATE T_SIF_INVESTIMENTO SET " +
                        "NOM_APLICACAO = ?, VAL_APLICACAO = ?, DES_INVESTIMENTO = ?, DAT_REALIZACAO = ?, DAT_VENCIMENTO = ? " +
                        "WHERE COD_INVESTIMENTO = ? AND COD_USUARIO = ?"
        )) {
            stm.setString(1, investimento.getNome());
            stm.setBigDecimal(2, investimento.getValor());
            stm.setString(3, investimento.getDescricao());
            stm.setDate(4, Date.valueOf(investimento.getDataRealizacao()));
            stm.setDate(5, Date.valueOf(investimento.getDataVencimento()));
            stm.setLong(6, investimento.getId());
            stm.setLong(7, investimento.getUsuarioId());

            int linhasAfetadas = stm.executeUpdate();

            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Investimento com ID " + investimento.getId() + " não foi encontrado para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        try(PreparedStatement stm = conexao.prepareStatement("DELETE FROM T_SIF_INVESTIMENTO WHERE COD_INVESTIMENTO = ?")) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Investimento não encontrado!");
        }
    }

    public void fecharConexao() throws SQLException {
        if(conexao != null) conexao.close();
    }
}