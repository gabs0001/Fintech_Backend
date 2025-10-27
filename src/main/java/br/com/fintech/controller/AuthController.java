package br.com.fintech.controller;

import br.com.fintech.dto.AuthResponse;
import br.com.fintech.dto.LoginRequest;
import br.com.fintech.dto.UsuarioResponse;
import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Usuario;
import br.com.fintech.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> cadastro(@RequestBody Usuario usuario) throws EntityNotFoundException, IllegalArgumentException {
        Usuario novoUsuario = usuarioService.insert(usuario);

        UsuarioResponse response = new UsuarioResponse(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws IllegalArgumentException {
        String jwtToken = usuarioService.login(request.getEmail(), request.getSenha());

        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }
}