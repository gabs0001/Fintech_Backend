package br.com.fintech.exceptions;

import br.com.fintech.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private ApiErrorDTO createErrorDTO(HttpStatus status, String message, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        return new ApiErrorDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }

    // ----------------------------------------------------
    // TRATAMENTO DE ENTITY NOT FOUND (404 Not Found)
    // ----------------------------------------------------

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorDTO> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request
    ) {
        ApiErrorDTO errorDetails = createErrorDTO(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // ----------------------------------------------------
    // TRATAMENTO DE ARGUMENTOS INVÁLIDOS (400 Bad Request)
    // ----------------------------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request
    ) {
        ApiErrorDTO errorDetails = createErrorDTO(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // ----------------------------------------------------
    // TRATAMENTO GENÉRICO (500 Internal Server Error)
    // ----------------------------------------------------

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorDTO> handleAllUncaughtException(
            Exception ex, WebRequest request
    ) {
        ex.printStackTrace();

        ApiErrorDTO errorDetails = createErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado. Por favor, tente novamente mais tarde.",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}