package br.com.fintech.controller;

import br.com.fintech.dto.ChangeEmailRequest;
import br.com.fintech.dto.ChangePasswordRequest;
import br.com.fintech.dto.UsuarioResponse;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Usuario;
import br.com.fintech.service.UsuarioService;
import br.com.fintech.service.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public UsuarioController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtService.getUserIdFromToken(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarPerfil(HttpServletRequest request)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Usuario usuario = usuarioService.getById(userId, userId);
        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    @PatchMapping("/email")
    public ResponseEntity<UsuarioResponse> alterarEmail(@RequestBody ChangeEmailRequest request, HttpServletRequest httpRequest)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(httpRequest);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Usuario usuarioParaAtualizar = usuarioService.changeEmail(userId, userId, request.getNovoEmail());
        return ResponseEntity.ok(new UsuarioResponse(usuarioParaAtualizar));
    }

    @PatchMapping("/senha")
    public ResponseEntity<Void> alterarSenha(@RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest)
            throws EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(httpRequest);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

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
    public ResponseEntity<Void> removerConta(HttpServletRequest request)
            throws SQLException, EntityNotFoundException, IllegalArgumentException {
        Long userId = getUsuarioId(request);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        usuarioService.remove(userId);
        return ResponseEntity.noContent().build();
    }
}