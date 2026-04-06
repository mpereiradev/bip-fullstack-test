package br.com.matheuspereira.bip.api.exception;

import br.com.matheuspereira.bip.api.dto.ErrorResponse;
import br.com.matheuspereira.bip.ejb.exception.BeneficioNotFoundException;
import br.com.matheuspereira.bip.ejb.exception.SaldoInsuficienteException;
import br.com.matheuspereira.bip.ejb.exception.TransferenciaInvalidaException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BeneficioNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(BeneficioNotFoundException ex) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Não encontrado", ex.getMessage());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return ErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Saldo insuficiente", ex.getMessage());
    }

    @ExceptionHandler(TransferenciaInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTransferenciaInvalida(TransferenciaInvalidaException ex) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Transferência inválida", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Dados inválidos", message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Dados inválidos", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno", "Ocorreu um erro inesperado");
    }
}
