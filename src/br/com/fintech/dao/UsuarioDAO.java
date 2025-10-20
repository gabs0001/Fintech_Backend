package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UsuarioDAO implements AutoCloseable {
    private final Connection conexao;

    public UsuarioDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public Usuario insert(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO T_SIF_USUARIO (COD_USUARIO, NOM_USUARIO, DAT_NASCIMENTO, DES_GENERO, TXT_EMAIL, TXT_SENHA) " +
                "VALUES (SEQ_SIF_USUARIO.NEXTVAL, ?,?,?,?,?)";

        try(PreparedStatement stm = conexao.prepareStatement(sql, new String[]{"COD_USUARIO"})) {
            stm.setString(1, usuario.getNome());
            stm.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stm.setString(3, usuario.getGenero());
            stm.setString(4, usuario.getEmail());
            stm.setString(5, usuario.getSenha());

            stm.executeUpdate();

            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    usuario.setId(novoId);
                }
            }

            return usuario;
        }
    }

    public Usuario getById(Long id) throws SQLException {
        String sql = "SELECT * FROM T_SIF_USUARIO WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            try(ResultSet result = stm.executeQuery()) {
                if(!result.next()) return null;
                return parseUsuario(result);
            }
        }
    }

    public Usuario getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM T_SIF_USUARIO WHERE TXT_EMAIL = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, email);

            try(ResultSet result = stm.executeQuery()) {
                if (!result.next()) return null;
                return parseUsuario(result);
            }
        }
    }

    public Usuario update(Long id, Usuario usuario) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_USUARIO SET " + "NOM_USUARIO = ?, DAT_NASCIMENTO = ?, DES_GENERO = ?, TXT_EMAIL = ?, TXT_SENHA = ? " +
                "WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, usuario.getNome());
            stm.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stm.setString(3, usuario.getGenero());
            stm.setString(4, usuario.getEmail());
            stm.setString(5, usuario.getSenha());
            stm.setLong(6, id);

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Usuário com ID " + id + " não foi encontrado para atualização!");
            }

            usuario.setId(id);
            return usuario;
        }
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        String sql = "DELETE FROM T_SIF_USUARIO WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, id);

            int linha = stm.executeUpdate();
            if (linha == 0) throw new EntityNotFoundException("Usuário para remover não encontrado!");
        }
    }

    private Usuario parseUsuario(ResultSet result) throws SQLException {
        Long id = result.getLong("COD_USUARIO");
        String nome = result.getString("NOM_USUARIO");
        Date dataNascimento = result.getDate("DAT_NASCIMENTO");
        String genero = result.getString("DES_GENERO");
        String email = result.getString("TXT_EMAIL");;
        String senha = result.getString("TXT_SENHA");;

        return new Usuario(id, nome, dataNascimento.toLocalDate(), genero, email, senha);
    }

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}