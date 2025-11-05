package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Usuario;
import br.com.fintech.repository.UsuarioRepository;
import br.com.fintech.service.security.JwtService;
import br.com.fintech.service.security.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private void validarUsuario(Usuario usuario, boolean isInsert) throws IllegalArgumentException {
        if(usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Nome do usuário é obrigatório!");
        }

        if(usuario.getDataNascimento() == null || usuario.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Erro: Usuário deve ter no mínimo 18 anos!");
        }

        Optional<Usuario> usuarioExistenteOpt = repository.findByEmail(usuario.getEmail());

        if(usuarioExistenteOpt.isPresent()) {
            Usuario usuarioExistente = usuarioExistenteOpt.get();

            if (isInsert || !usuarioExistente.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Erro: E-mail já cadastrado!");
            }
        }

        if(isInsert && isPasswordInvalid(usuario.getSenha())) {
            throw new IllegalArgumentException("Erro: A senha deve ter no mínimo 6 caracteres, combinar letras e números!");
        }
    }

    private boolean isPasswordInvalid(String senha) {
        if(senha == null || senha.length() < 6) {
            return true;
        }

        boolean contemLetra = senha.matches(".*[a-zA-Z].*");
        boolean contemNumero = senha.matches(".*[0-9].*");

        return !(contemLetra && contemNumero);
    }

    public Usuario getById(Long entityId, Long userId) throws EntityNotFoundException, IllegalArgumentException {
        if(!entityId.equals(userId)) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para buscar outros perfis!");
        }

        return repository.findById(entityId).orElseThrow(() ->
                new EntityNotFoundException("Perfil de usuário não encontrado.")
        );
    }

    public Usuario insert(Usuario novoUsuario) throws IllegalArgumentException {
        validarUsuario(novoUsuario, true);

        String senhaHash = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaHash);

        return repository.save(novoUsuario);
    }

    public Usuario update(Long userId, Usuario usuarioParaAlterar) throws EntityNotFoundException, IllegalArgumentException {
        if (!userId.equals(usuarioParaAlterar.getId())) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para alterar o perfil de terceiros!");
        }

        Usuario usuarioAtual = repository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Usuário não encontrado para alteração.")
        );

        usuarioParaAlterar.setSenha(usuarioAtual.getSenha());
        usuarioParaAlterar.setId(userId);

        validarUsuario(usuarioParaAlterar, false);

        return repository.save(usuarioParaAlterar);
    }

    public void remove(Long id) throws EntityNotFoundException {
        repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Usuário não encontrado para remoção.")
        );

        repository.deleteById(id);
    }

    public Usuario changeEmail(Long idEntity, Long userId, String novoEmail) throws EntityNotFoundException, IllegalArgumentException {
        if (!idEntity.equals(userId)) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para alterar o e-mail de terceiros!");
        }

        Usuario usuario = repository.findById(idEntity).orElseThrow(() ->
                new EntityNotFoundException("Usuário não encontrado para alteração de e-mail.")
        );

        if(novoEmail == null || novoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Novo e-mail não pode ser vazio.");
        }

        Optional<Usuario> usuarioComNovoEmailOpt = repository.findByEmail(novoEmail);
        if(usuarioComNovoEmailOpt.isPresent() && !usuarioComNovoEmailOpt.get().getId().equals(idEntity)) {
            throw new IllegalArgumentException("Erro: E-mail já está em uso por outro usuário!");
        }

        usuario.setEmail(novoEmail);

        return repository.save(usuario);
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

    public Usuario changePassword(Long idEntity, Long userId, String senhaAntiga, String novaSenha1, String novaSenha2) throws EntityNotFoundException, IllegalArgumentException {
        if (!idEntity.equals(userId)) {
            throw new IllegalArgumentException("Erro: Usuário não tem permissão para alterar a senha de terceiros!");
        }

        Usuario usuario = repository.findById(idEntity).orElseThrow(() ->
                new EntityNotFoundException("Usuário não encontrado para alteração de senha.")
        );

        validatePasswordChange(usuario.getSenha(), senhaAntiga, novaSenha1, novaSenha2);

        String novaSenhaHash = passwordEncoder.encode(novaSenha1);
        usuario.setSenha(novaSenhaHash);

        return repository.save(usuario);
    }

    public String login(String email, String senha) throws IllegalArgumentException {
        Usuario usuario = repository.findByEmail(email).orElse(null);

        if(usuario == null) {
            throw new IllegalArgumentException("Erro: Credenciais inválidas (e-mail ou senha incorretos).");
        }

        if(!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Erro: Credenciais inválidas (e-mail ou senha incorretos).");
        }

        return jwtService.generateToken(usuario);
    }
}