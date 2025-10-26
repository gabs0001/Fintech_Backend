package br.com.fintech.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entidade não foi encontrada ou não pertence ao usuário")
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) { super(message); }
}