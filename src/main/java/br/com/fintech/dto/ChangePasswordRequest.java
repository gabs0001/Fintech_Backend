package br.com.fintech.dto;

public class ChangePasswordRequest {
    private String senhaAntiga;
    private String novaSenha1;
    private String novaSenha2;

    public String getSenhaAntiga() { return senhaAntiga; }

    public void setSenhaAntiga(String senhaAntiga) {
        this.senhaAntiga = senhaAntiga;
    }

    public String getNovaSenha1() {
        return novaSenha1;
    }

    public void setNovaSenha1(String novaSenha1) {
        this.novaSenha1 = novaSenha1;
    }

    public String getNovaSenha2() {
        return novaSenha2;
    }

    public void setNovaSenha2(String novaSenha2) {
        this.novaSenha2 = novaSenha2;
    }
}
