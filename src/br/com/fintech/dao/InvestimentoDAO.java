package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Categoria;
import br.com.fintech.model.Instituicao;
import br.com.fintech.model.Investimento;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InvestimentoDAO implements CrudDAO<Investimento, Long>, AutoCloseable {
    private final Connection conexao;

    public InvestimentoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public Investimento insert(Investimento investimento) throws SQLException {
        String sql = "INSERT INTO T_SIF_INVESTIMENTO (" +
                "COD_INVESTIMENTO, NOM_APLICACAO, VAL_APLICACAO, DES_INVESTIMENTO, DAT_REALIZACAO, DAT_VENCIMENTO, " +
                "COD_USUARIO, COD_TIPO_INVESTIMENTO, COD_INSTITUICAO) " +
                "VALUES (SEQ_SIF_INVESTIMENTO.NEXTVAL,?,?,?,?,?,?,?,?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{"COD_INVESTIMENTO"})) {
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

            return investimento;
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

        Categoria categoria = new Categoria(categoriaId, null);
        Instituicao instituicao = new Instituicao(instituicaoId, null);

        return new Investimento(
                id,
                usuarioId,
                descricao,
                categoria,
                valor,
                nome,
                dataRealizacao != null ? dataRealizacao.toLocalDate() : null,
                dataVencimento != null ? dataVencimento.toLocalDate() : null,
                instituicao
        );
    }

    public List<Investimento> getAllByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM T_SIF_INVESTIMENTO WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);

            List<Investimento> investimentos = new ArrayList<>();

            try(ResultSet result = stm.executeQuery()) {
                while (result.next()) {
                    investimentos.add(parseInvestimento(result));
                }
                return investimentos;
            }
        }
    }

    public Investimento getById(Long idEntity, Long idUser) throws SQLException {
        String sql = "SELECT * FROM T_SIF_INVESTIMENTO WHERE COD_INVESTIMENTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(2, idUser);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseInvestimento(result);
            }
        }
    }

    public Investimento update(Long userId, Investimento investimento) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_INVESTIMENTO SET " + "NOM_APLICACAO = ?, VAL_APLICACAO = ?, DES_INVESTIMENTO = ?, DAT_REALIZACAO = ?, " +
                "DAT_VENCIMENTO = ?, " + "COD_INSTITUICAO = ?, COD_TIPO_INVESTIMENTO = ? " +
                "WHERE COD_INVESTIMENTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, investimento.getNome());
            stm.setBigDecimal(2, investimento.getValor());
            stm.setString(3, investimento.getDescricao());
            stm.setDate(4, Date.valueOf(investimento.getDataRealizacao()));
            stm.setDate(5, Date.valueOf(investimento.getDataVencimento()));
            stm.setLong(6, investimento.getInstituicaoId());
            stm.setLong(7, investimento.getCategoriaId());
            stm.setLong(8, investimento.getId());
            stm.setLong(9, userId);

            int linhasAfetadas = stm.executeUpdate();
            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Investimento com ID " + investimento.getId() + " não foi encontrado para atualização!");
            }

            return investimento;
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_INVESTIMENTO WHERE COD_INVESTIMENTO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Investimento não encontrado!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}