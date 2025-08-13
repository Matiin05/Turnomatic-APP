package com.turnomatic.backend.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.turnomatic.backend.exception.Negocio.NegocioDuplicateException;
import com.turnomatic.backend.exception.Negocio.NegocioNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegocioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNegocioNotFound(
            NegocioNotFoundException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            "NOT_FOUND",
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(NegocioDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleNegocioDuplicate(
            NegocioDuplicateException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            "CONFLICT",
            HttpStatus.CONFLICT.value(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogic(
            BusinessLogicException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            "UNPROCESSABLE_ENTITY",
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(
            "Errores de validación: " + errors,
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            "Error interno del servidor: " + ex.getMessage(),
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
