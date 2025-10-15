package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class CategoriaBaseDAO implements CrudDAO<Categoria, Long>, AutoCloseable {
    protected final String nomeTabela;
    protected final String nomePk;
    protected final String nomeSequencia;
    protected final String descricaoColuna;
    protected final Connection conexao;

    public CategoriaBaseDAO(String nomeTabela, String nomePK, String nomeSequencia, String descricaoColuna) throws SQLException {
        this.nomeTabela = nomeTabela;
        this.nomePk = nomePK;
        this.nomeSequencia = nomeSequencia;
        this.descricaoColuna = descricaoColuna;
        this.conexao = ConnectionFactory.getConnection();
    }

    public void insert(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO " + this.nomeTabela + "(" + this.nomePk + ", " + this.descricaoColuna + ") VALUES (" + this.nomeSequencia +
                ".NEXTVAL, ?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{ this.nomePk })) {
            stm.setString(1, categoria.getDescricao());

            stm.executeUpdate();

            try(ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    categoria.setId(novoId);
                }
            }
        }
    }

    public Categoria parseCategoria(ResultSet result) throws SQLException {
        Long id = result.getLong(this.nomePk);
        String descricao = result.getString(this.descricaoColuna);

        return new Categoria(id, descricao);
    }

    public List<Categoria> getAll() throws SQLException {
        String sql = "SELECT * FROM " + this.nomeTabela;

        try(PreparedStatement stm = conexao.prepareStatement(sql);
            ResultSet result = stm.executeQuery()
        ) {
            List<Categoria> categorias = new ArrayList<>();

            while(result.next()) {
                categorias.add(parseCategoria(result));
            }

            return categorias;
        }
    }

    public Categoria getById(Long entityId, Long userId) throws SQLException {
        String sql = "SELECT * FROM " + this.nomeTabela + " WHERE " + this.nomePk + " = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, entityId);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseCategoria(result);
            }
        }
    }

    public void update(Categoria categoria) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE " + this.nomeTabela + " SET " + this.descricaoColuna + " = ? WHERE " + this.nomePk + " = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, categoria.getDescricao());
            stm.setLong(2, categoria.getId());

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Categoria com ID " + categoria.getId() + " não foi encontrada para atualização!");
            }
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM " + this.nomeTabela + " WHERE " + this.nomePk + " = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Categoria não encontrada!");
        }
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}