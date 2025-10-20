package br.com.fintech.service;

import br.com.fintech.dao.UsuarioDAO;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Usuario;
import br.com.fintech.service.security.JwtService;
import br.com.fintech.service.security.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

@Service
public class UsuarioService {
    private final UsuarioDAO dao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioDAO dao, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private void validarUsuario(Usuario usuario, boolean isInsert) throws IllegalArgumentException, SQLException {
        if(usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Nome do usuário é obrigatório!");
        }

        if(usuario.getDataNascimento() == null || usuario.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Erro: Usuário deve ter no mínimo 18 anos!");
        }

        Usuario usuarioExistente = dao.getByEmail(usuario.getEmail());

        if(usuarioExistente != null && (isInsert || !usuarioExistente.getId().equals(usuario.getId()))) {
            throw new IllegalArgumentException("Erro: E-mail já cadastrado!");
        }

        if(isInsert && isPasswordInvalid(usuario.getSenha())) {
            throw new IllegalArgumentException("Erro: A senha deve ter no mínimo 6 caracteres, combinar letras e números!");
        }
    }

    private boolean isPasswordInvalid(String senha) {
        if(senha == null || senha.length() < 6) {
            return true;
        }

        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9]).+$");

        return !p.matcher(senha).matches();
    }

    public Usuario getById(Long entityId, Long userId) throws SQLException, EntityNotFoundException {
        if(!entityId.equals(userId)) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para buscar outros perfis!");
        }

        Usuario usuario = dao.getById(entityId);
        if (usuario == null) {
            throw new EntityNotFoundException("Perfil de usuário não encontrado.");
        }

        return usuario;
    }

    public Usuario insert(Usuario novoUsuario) throws SQLException {
        validarUsuario(novoUsuario, true);

        String senhaHash = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaHash);

        return dao.insert(novoUsuario);
    }

    public Usuario update(Long userId, Usuario usuarioParaAlterar) throws SQLException, EntityNotFoundException {
        if (!userId.equals(usuarioParaAlterar.getId())) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para alterar o perfil de terceiros!");
        }

        Usuario usuarioAtual = dao.getById(userId);

        if (usuarioAtual == null) {
            throw new EntityNotFoundException("Usuário não encontrado para alteração.");
        }

        usuarioParaAlterar.setSenha(usuarioAtual.getSenha());

        validarUsuario(usuarioParaAlterar, false);

        return dao.update(userId, usuarioParaAlterar);
    }

    public void remove(Long id) throws SQLException, EntityNotFoundException {
        dao.remove(id);
    }

    public Usuario changeEmail(Long idEntity, Long userId, String novoEmail) throws SQLException, EntityNotFoundException, IllegalArgumentException {
        if (!idEntity.equals(userId)) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para alterar o e-mail de terceiros!");
        }

        Usuario usuario = dao.getById(idEntity);

        if (usuario == null) {
            throw new EntityNotFoundException("Usuário não encontrado para alteração de e-mail.");
        }

        if(novoEmail == null || novoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Novo e-mail não pode ser vazio.");
        }

        Usuario usuarioComNovoEmail = dao.getByEmail(novoEmail);
        if(usuarioComNovoEmail != null && !usuarioComNovoEmail.getId().equals(idEntity)) {
            throw new IllegalArgumentException("Erro: E-mail já está em uso por outro usuário!");
        }

        usuario.setEmail(novoEmail);

        return dao.update(userId, usuario);
    }

    private void validatePasswordChange(String senhaAtualHash, String senhaAntigaTexto, String novaSenha1, String novaSenha2) throws IllegalArgumentException {
        if(!passwordEncoder.matches(senhaAntigaTexto, senhaAtualHash)) {
            throw new IllegalArgumentException("Erro: Senha antiga está incorreta!");
        }

        if(!novaSenha1.equals(novaSenha2)) {
            throw new IllegalArgumentException("Erro: As novas senhas não correspondem!");
        }

        if(passwordEncoder.matches(novaSenha1, senhaAtualHash)) {
            throw new IllegalArgumentException("Erro: A nova senha não pode ser igual à senha antiga!");
        }

        if(isPasswordInvalid(novaSenha1)) {
            throw new IllegalArgumentException("Erro: A nova senha deve ter no mínimo 6 caracteres e combinar letras e números!");
        }
    }

    public Usuario changePassword(Long idEntity, Long userId, String senhaAntiga, String novaSenha1, String novaSenha2) throws SQLException, EntityNotFoundException {
        if (!idEntity.equals(userId)) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para alterar a senha de terceiros!");
        }

        Usuario usuario = dao.getById(idEntity);

        if(usuario == null) {
            throw new EntityNotFoundException("Usuário não encontrado para alteração de senha.");
        }

        validatePasswordChange(usuario.getSenha(), senhaAntiga, novaSenha1, novaSenha2);

        String novaSenhaHash = passwordEncoder.encode(novaSenha1);
        usuario.setSenha(novaSenhaHash);

        return dao.update(userId, usuario);
    }

    public String login(String email, String senha) throws SQLException, IllegalArgumentException {
        Usuario usuario = dao.getByEmail(email);

        if(usuario == null) {
            throw new IllegalArgumentException("Erro: Credenciais inválidas (e-mail ou senha incorretos).");
        }

        if(!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Erro: Credenciais inválidas (e-mail ou senha incorretos).");
        }

        return jwtService.generateToken(usuario);
    }
}