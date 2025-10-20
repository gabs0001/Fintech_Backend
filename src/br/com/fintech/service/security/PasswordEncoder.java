package br.com.fintech.service.security;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Component
public class PasswordEncoder {
    public String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for(byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(NoSuchAlgorithmException e) {
            System.err.println("Algoritmo de Hash não encontrado. Grave Risco de Segurança!");
            return rawPassword;
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if(rawPassword == null || encodedPassword == null) {
            return false;
        }
        String hashedRawPassword = encode(rawPassword);
        return hashedRawPassword.equals(encodedPassword);
    }
}
