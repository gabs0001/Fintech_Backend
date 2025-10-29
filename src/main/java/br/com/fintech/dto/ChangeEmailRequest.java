package br.com.fintech.dto;

public class ChangeEmailRequest {
    private String novoEmail;

    public String getNovoEmail() { return novoEmail; }

    public void setNovoEmail(String novoEmail) {
        this.novoEmail = novoEmail;
    }

    public ChangeEmailRequest(String novoEmail) {
        this.novoEmail = novoEmail;
    }
}