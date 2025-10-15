package br.com.fintech.dao;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.factory.ConnectionFactory;
import br.com.fintech.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements CrudDAO<Usuario, Long>, AutoCloseable {
    private final Connection conexao;

    public UsuarioDAO() throws SQLException {
        conexao = ConnectionFactory.getConnection();
    }

    public void insert(Usuario usuario) throws SQLException {
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
                if (generatedKeys.next()) {
                    Long novoId = generatedKeys.getLong(1);
                    usuario.setId(novoId);
                }
            }
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

    public List<Usuario> getAll() throws SQLException {
        String sql = "SELECT * FROM T_SIF_USUARIO";

        try(PreparedStatement stm = conexao.prepareStatement(sql);
            ResultSet result = stm.executeQuery()) {

            List<Usuario> usuarios = new ArrayList<>();

            while (result.next()) {
                usuarios.add(parseUsuario(result));
            }

            return usuarios;
        }
    }

    public Usuario getById(Long idEntity, Long idUser) throws SQLException {
        String sql = "SELECT * FROM T_SIF_USUARIO WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setLong(1, idEntity);

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

    public void update(Usuario usuario) throws SQLException, EntityNotFoundException {
        String sql = "UPDATE T_SIF_USUARIO SET " + "NOM_USUARIO = ?, DAT_NASCIMENTO = ?, DES_GENERO = ?, TXT_EMAIL = ?, TXT_SENHA = ? " +
                "WHERE COD_USUARIO = ?";

        try(PreparedStatement stm = conexao.prepareStatement(sql)) {
            stm.setString(1, usuario.getNome());
            stm.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stm.setString(3, usuario.getGenero());
            stm.setString(4, usuario.getEmail());
            stm.setString(5, usuario.getSenha());

            int linhasAfetadas = stm.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntityNotFoundException("Erro: Gasto com ID " + usuario.getId() + " não foi encontrado para atualização!");
            }
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

    @Override
    public void close() throws SQLException {
        if(conexao != null) conexao.close();
    }
}