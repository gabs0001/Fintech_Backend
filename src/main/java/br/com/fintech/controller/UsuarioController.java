package br.com.fintech.controller;

import br.com.fintech.dto.ChangeEmailRequest;
import br.com.fintech.dto.ChangePasswordRequest;
import br.com.fintech.dto.UsuarioResponse;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Usuario;
import br.com.fintech.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private static final Long MOCK_USER_ID = 1L;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarPerfil() throws EntityNotFoundException, IllegalArgumentException {
        Long userId = MOCK_USER_ID;

        Usuario usuario = usuarioService.getById(userId, userId);

        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    @PatchMapping("/email")
    public ResponseEntity<UsuarioResponse> alterarEmail(@RequestBody ChangeEmailRequest request) throws EntityNotFoundException, IllegalArgumentException {
        Long userId = MOCK_USER_ID;

        Usuario usuarioParaAtualizar = usuarioService.changeEmail(userId, userId, request.getNovoEmail());

        return ResponseEntity.ok(new UsuarioResponse(usuarioParaAtualizar));
    }

    @PatchMapping("/senha")
    public ResponseEntity<Void> alterarSenha(@RequestBody ChangePasswordRequest request) throws EntityNotFoundException, IllegalArgumentException {
        Long userId = MOCK_USER_ID;

        usuarioService.changePassword(
                userId,
                userId,
                request.getSenhaAntiga(),
                request.getNovaSenha1(),
                request.getNovaSenha2()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> removerConta() throws SQLException, EntityNotFoundException, IllegalArgumentException {
        Long userId = MOCK_USER_ID;

        usuarioService.remove(userId);

        return ResponseEntity.noContent().build();
    }
}