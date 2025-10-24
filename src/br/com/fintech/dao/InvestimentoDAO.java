package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Instituicao;
import br.com.fintech.model.Investimento;
import br.com.fintech.model.TipoInvestimento;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InvestimentoDAO implements CrudDAO<Investimento, Long>, AutoCloseable {
    private final Connection conexao;

    public InvestimentoDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public BigDecimal calcularTotal(Long userId) throws SQLException {
        String sql = "SELECT SUM(VAL_APLICACAO) AS TOTAL FROM T_SIF_INVESTIMENTO WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);

            try(ResultSet result = stm.executeQuery()) {
                if (result.next()) {
                    // Retorna 0 se a soma for NULL (usuário sem investimentos)
                    return result.getBigDecimal("TOTAL") != null ? result.getBigDecimal("TOTAL") : BigDecimal.ZERO;
                }
                return BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "SELECT SUM(VAL_APLICACAO) AS TOTAL FROM T_SIF_INVESTIMENTO WHERE COD_USUARIO = ? AND DAT_REALIZACAO BETWEEN ? AND ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);
            stm.setDate(2, Date.valueOf(inicio));
            stm.setDate(3, Date.valueOf(fim));

            try(ResultSet result = stm.executeQuery()) {
                if (result.next()) {
                    // Retorna 0 se a soma for NULL
                    return result.getBigDecimal("TOTAL") != null ? result.getBigDecimal("TOTAL") : BigDecimal.ZERO;
                }
                return BigDecimal.ZERO;
            }
        }
    }

    public Investimento getUltimo(Long userId) throws SQLException {
        // Usa a query de JOIN complexa
        String sql = "SELECT I.*, T.DES_TIPO_INVESTIMENTO, N.NOM_INSTITUICAO " +
                "FROM T_SIF_INVESTIMENTO I " +
                "JOIN T_SIF_TIPO_INVESTIMENTO T ON I.COD_TIPO_INVESTIMENTO = T.COD_TIPO_INVESTIMENTO " +
                "JOIN T_SIF_INSTITUICAO N ON I.COD_INSTITUICAO = N.COD_INSTITUICAO " +
                "WHERE I.COD_USUARIO = ? ORDER BY I.DAT_REALIZACAO DESC FETCH FIRST 1 ROW ONLY";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);

            try(ResultSet result = stm.executeQuery()) {
                if (!result.next()) return null;
                return parseInvestimento(result);
            }
        }
    }

    public List<Investimento> getUltimos(Long userId, int limite) throws SQLException {
        String sql = "SELECT I.*, T.DES_TIPO_INVESTIMENTO, N.NOM_INSTITUICAO " +
                "FROM T_SIF_INVESTIMENTO I " +
                "JOIN T_SIF_TIPO_INVESTIMENTO T ON I.COD_TIPO_INVESTIMENTO = T.COD_TIPO_INVESTIMENTO " +
                "JOIN T_SIF_INSTITUICAO N ON I.COD_INSTITUICAO = N.COD_INSTITUICAO " +
                "WHERE I.COD_USUARIO = ? ORDER BY I.DAT_REALIZACAO DESC FETCH NEXT ? ROWS ONLY";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, userId);
            stm.setInt(2, limite);

            List<Investimento> investimentos = new ArrayList<>();

            try(ResultSet result = stm.executeQuery()) {
                while (result.next()) {
                    investimentos.add(parseInvestimento(result));
                }

                return investimentos;
            }
        }
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

            if(investimento.getDataVencimento() != null) {
                stm.setDate(5, Date.valueOf(investimento.getDataVencimento()));
            } else {
                stm.setNull(5, Types.DATE);
            }

            stm.setLong(6, investimento.getUsuarioId());
            stm.setLong(7, investimento.getTipoInvestimentoId());
            stm.setLong(8, investimento.getInstituicaoId());

            stm.executeUpdate();

            try(ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    investimento.setId(novoId);
                } else {
                    throw new SQLException("Falha ao obter o ID gerado para o Investimento. Nenhuma chave retornada");
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

        Long tipoInvestimentoId = result.getLong("COD_TIPO_INVESTIMENTO");
        String desTipoInvestimento = result.getString("DES_TIPO_INVESTIMENTO");
        TipoInvestimento tipoInvestimento = new TipoInvestimento(tipoInvestimentoId, desTipoInvestimento);

        Long instituicaoId = result.getLong("COD_INSTITUICAO");
        String nomeInstituicao = result.getString("NOM_INSTITUICAO");
        Instituicao instituicao = new Instituicao(instituicaoId, nomeInstituicao);

        return new Investimento(
                id,
                usuarioId,
                descricao,
                tipoInvestimento,
                valor,
                nome,
                dataRealizacao != null ? dataRealizacao.toLocalDate() : null,
                dataVencimento != null ? dataVencimento.toLocalDate() : null,
                instituicao
        );
    }

    public List<Investimento> getAllByUserId(Long ownerId) throws SQLException {
        String sql = "SELECT I.*, T.DES_TIPO_INVESTIMENTO, N.NOM_INSTITUICAO " +
                "FROM T_SIF_INVESTIMENTO I " +
                "JOIN T_SIF_TIPO_INVESTIMENTO T ON I.COD_TIPO_INVESTIMENTO = T.COD_TIPO_INVESTIMENTO " +
                "JOIN T_SIF_INSTITUICAO N ON I.COD_INSTITUICAO = N.COD_INSTITUICAO " +
                "WHERE I.COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, ownerId);

            List<Investimento> investimentos = new ArrayList<>();

            try(ResultSet result = stm.executeQuery()) {
                while (result.next()) {
                    investimentos.add(parseInvestimento(result));
                }

                return investimentos;
            }
        }
    }

    public Investimento getById(Long idEntity, Long ownerId) throws SQLException {
        String sql = "SELECT I.*, T.DES_TIPO_INVESTIMENTO, N.NOM_INSTITUICAO " +
                "FROM T_SIF_INVESTIMENTO I " +
                "JOIN T_SIF_TIPO_INVESTIMENTO T ON I.COD_TIPO_INVESTIMENTO = T.COD_TIPO_INVESTIMENTO " +
                "JOIN T_SIF_INSTITUICAO N ON I.COD_INSTITUICAO = N.COD_INSTITUICAO " +
                "WHERE I.COD_INVESTIMENTO = ? AND I.COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(2, ownerId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseInvestimento(result);
            }
        }
    }

    public Investimento update(Long ownerId, Investimento investimento) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_INVESTIMENTO SET " + "NOM_APLICACAO = ?, VAL_APLICACAO = ?, DES_INVESTIMENTO = ?, DAT_REALIZACAO = ?, " +
                "DAT_VENCIMENTO = ?, " + "COD_INSTITUICAO = ?, COD_TIPO_INVESTIMENTO = ? " +
                "WHERE COD_INVESTIMENTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, investimento.getNome());
            stm.setBigDecimal(2, investimento.getValor());
            stm.setString(3, investimento.getDescricao());
            stm.setDate(4, Date.valueOf(investimento.getDataRealizacao()));

            if(investimento.getDataVencimento() != null) {
                stm.setDate(5, Date.valueOf(investimento.getDataVencimento()));
            } else {
                stm.setNull(5, Types.DATE);
            }

            stm.setLong(6, investimento.getInstituicaoId());
            stm.setLong(7, investimento.getTipoInvestimentoId());
            stm.setLong(8, investimento.getId());
            stm.setLong(9, ownerId);

            int linhasAfetadas = stm.executeUpdate();
            if(linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Investimento com ID " + investimento.getId() + " não foi encontrado para atualização!");
            }

            return investimento;
        }
    }

    public void remove(Long idEntity, Long ownerId) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_INVESTIMENTO WHERE COD_INVESTIMENTO = ? AND COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);
            stm.setLong(2, ownerId);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Investimento não encontrado!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}