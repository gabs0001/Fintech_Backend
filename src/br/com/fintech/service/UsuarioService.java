package br.com.fintech.service;

import br.com.fintech.dao.UsuarioDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class UsuarioService extends CrudService<Usuario, Long>{

    public UsuarioService(UsuarioDAO usuarioDAO) { super(usuarioDAO); }

    public UsuarioDAO getUsuarioDAO() { return (UsuarioDAO) this.dao; }

    private void validarUsuario(Usuario usuario, boolean isInsert) throws IllegalArgumentException, SQLException {
        if(usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Nome do usuário é obrigatório!");
        }

        if(usuario.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Erro: Usuário deve ter no mínimo 18 anos!");
        }

        Usuario usuarioExistente = getUsuarioDAO().getByEmail(usuario.getEmail());

        if(usuarioExistente != null && (isInsert || !usuarioExistente.getId().equals(usuario.getId()))) {
            throw new IllegalArgumentException("Erro: E-mail já cadastrado!");
        }

        if(usuario.getSenha() != null && validateFormatPassword(usuario.getSenha())) {
            throw new IllegalArgumentException("Erro: A senha deve ter no mínimo 6 caracteres, combinar letras e números!");
        }
    }

    private boolean validateFormatPassword(String senha) {
        if(senha == null || senha.length() < 6) {
            return false;
        }

        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9]).+$");
        return p.matcher(senha).matches();
    }

    public Usuario getById(Long entityId, Long userId) throws SQLException {
        if(!entityId.equals(userId)) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para buscar outros perfis!");
        }
        return super.getById(entityId,userId);
    }

    public void insert(Usuario novoUsuario) throws SQLException {
        validarUsuario(novoUsuario, true);

        /*
            criptografar senha aqui!
        */

        super.insert(novoUsuario);
    }

    public void update(Usuario usuarioParaAlterar) throws SQLException, EntityNotFoundException {
        validarUsuario(usuarioParaAlterar, false);
        super.update(usuarioParaAlterar);
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        dao.remove(id);
    }

    public void changeEmail(Long idEntity, String novoEmail) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        Usuario usuario = fetchOrThrowException(idEntity, idEntity);

        if(novoEmail == null || novoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Novo e-mail não pode ser vazio.");
        }

        Usuario usuarioComNovoEmail = getUsuarioDAO().getByEmail(novoEmail);
        if(usuarioComNovoEmail != null && !usuarioComNovoEmail.getId().equals(idEntity)) {
            throw new IllegalArgumentException("Erro: E-mail já está em uso por outro usuário!");
        }

        usuario.setEmail(novoEmail);
        super.update(usuario);
    }

    private void validatePassword(Usuario usuario, String senhaAntiga, String novaSenha1, String novaSenha2) throws IllegalArgumentException {
        if(!usuario.getSenha().equals(senhaAntiga)) {
            throw new IllegalArgumentException("Erro: Senha antiga está incorreta!");
        }

        if(!novaSenha1.equals(novaSenha2)) {
            throw new IllegalArgumentException("Erro: As novas senhas não correspondem!");
        }

        if(novaSenha1.equals(senhaAntiga)) {
            throw new IllegalArgumentException("Erro: A nova senha não pode ser igual à senha antiga!");
        }

        if(!validateFormatPassword(novaSenha2)) {
            throw new IllegalArgumentException("Erro: A nova senha deve ter no mínimo 6 caracteres e combinar letras e números!");
        }
    }

    public void changePassword(Long idEntity, String senhaAntiga, String novaSenha1, String novaSenha2) throws SQLException, EntityNotFoundException {
        Usuario usuario = fetchOrThrowException(idEntity, idEntity);

        validatePassword(usuario, senhaAntiga, novaSenha1, novaSenha2);

        /*
            criptografar senha aqui!
        */

        usuario.setSenha(novaSenha1);
        super.update(usuario);
    }
}