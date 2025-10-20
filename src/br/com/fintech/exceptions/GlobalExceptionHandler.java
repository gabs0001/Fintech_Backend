package br.com.fintech.exceptions;

import br.com.fintech.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {
    // ----------------------------------------------------
    // TRATAMENTO DE ENTITY NOT FOUND (404 Not Found)
    // ----------------------------------------------------

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request
    ) {
        String path = request.getDescription(false).replace("uri=", "");
        HttpStatus status = HttpStatus.NOT_FOUND;

        ApiErrorDTO errorDetails = new ApiErrorDTO(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                path
        );

        return new ResponseEntity<>(errorDetails, status);
    }

    // ----------------------------------------------------
    // TRATAMENTO DE ERROS DE BANCO (500 Internal Server Error)
    // ----------------------------------------------------

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiErrorDTO> handleSQLException(
            SQLException ex, WebRequest request
    ) {
        String path = request.getDescription(false).replace("uri=", "");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiErrorDTO errorDetails = new ApiErrorDTO(
                status.value(),
                status.getReasonPhrase(),
                "Erro interno ao processar a solicitação no banco de dados.",
                path
        );

        // Loga o erro completo no console do servidor
        System.err.println("SQL Exception: " + ex.getMessage());

        return new ResponseEntity<>(errorDetails, status);
    }
}