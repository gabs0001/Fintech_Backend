package br.com.fintech.service.security;

import br.com.fintech.model.Usuario;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private static final String SECRET_KEY = "chave@!super!@secreta@@!";
    private static final long EXPIRATION_TIME_HOURS = 1;
    private static final String USER_ID_CLAIM = "idUsuario";

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        Date expirationDate = Date.from(now.plus(EXPIRATION_TIME_HOURS, ChronoUnit.HOURS));

        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID_CLAIM, usuario.getId());
        claims.put("email", usuario.getEmail());

        String tokenPayload = String.format("{\"%s\":%d,\"email\":\"%s\",\"exp\":\"%s\"}",
                USER_ID_CLAIM, usuario.getId(), usuario.getEmail(), expirationDate.getTime());

        return "mock_token_header." + java.util.Base64.getEncoder().encodeToString(tokenPayload.getBytes())
                + ".mock_signature";
    }

    public Long getUserIdFromToken(String token) {
        if (token == null || !token.contains(".")) {
            System.err.println("Token inválido ou em formato incorreto.");
            return null;
        }

        try {
            String[] parts = token.split("\\.");
            String payloadBase64 = parts[1];

            String payloadJson = new String(java.util.Base64.getDecoder().decode(payloadBase64));

            if (payloadJson.contains(USER_ID_CLAIM)) {
                String searchString = "\"" + USER_ID_CLAIM + "\":";
                int startIndex = payloadJson.indexOf(searchString) + searchString.length();
                int endIndex = payloadJson.indexOf(",", startIndex);
                if (endIndex == -1) endIndex = payloadJson.indexOf("}", startIndex);

                String idString = payloadJson.substring(startIndex, endIndex);

                if (isTokenExpired(payloadJson)) {
                    System.err.println("Token expirado.");
                    return null;
                }

                return Long.parseLong(idString);
            }
            return null;
        }
        catch (Exception e) {
            System.err.println("Erro ao processar/validar o token: " + e.getMessage());
            return null;
        }
    }

    private boolean isTokenExpired(String payloadJson) {
        try {
            String searchString = "\"exp\":";
            int startIndex = payloadJson.indexOf(searchString) + searchString.length();

            String expString = payloadJson.substring(startIndex, payloadJson.indexOf("}", startIndex));
            long expirationTimeMillis = Long.parseLong(expString);

            return expirationTimeMillis < System.currentTimeMillis();
        }
        catch (Exception e) {
            return false;
        }
    }
}