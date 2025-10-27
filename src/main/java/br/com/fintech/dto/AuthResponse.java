package br.com.fintech.dto;

public class AuthResponse {
    private String token;
    private Long idUsuario;
    private String nomeUsuario;

    public AuthResponse() {}

    public AuthResponse(String token, Long idUsuario, String nomeUsuario) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public Long getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }

    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
}
