package com.dppl.mycards.card.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dppl.mycards.card.service.dto.ErrorDTO;
import com.dppl.mycards.card.service.dto.ResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<?>> handleCommonException(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Technical Error");
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ResponseDTO<?>> handleDuplicateUserException(DuplicateResourceException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Duplicate Entry");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseDTO<?>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Invalid Credentials");
    }

    
    
    
    
    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<ResponseDTO<?>> handleBadGatewayException(BadGatewayException ex) {
    	return buildErrorResponse(ex, HttpStatus.BAD_GATEWAY, HttpStatus.BAD_GATEWAY.toString());
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDTO<?>> handleBadRequestException(BadRequestException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.toString());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseDTO<?>> handleConflictException(ConflictException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, HttpStatus.CONFLICT.toString());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseDTO<?>> handleForbiddenException(ForbiddenException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.toString());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ResponseDTO<?>> handleInternalServerErrorException(InternalServerErrorException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDTO<?>> handleNotFoundException(NotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.toString());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ResponseDTO<?>> handleServiceUnavailableException(ServiceUnavailableException ex) {
        return buildErrorResponse(ex, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE.toString());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ResponseDTO<?>> handleTooManyRequestsException(TooManyRequestsException ex) {
        return buildErrorResponse(ex, HttpStatus.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS.toString());
    }

    private ResponseEntity<ResponseDTO<?>> buildErrorResponse(Exception ex, HttpStatus status, String title) {
        ResponseDTO<?> responseDTO = new ResponseDTO<>();

        List<ErrorDTO> errorList = new ArrayList<>();
        errorList.add(new ErrorDTO(
                UUID.randomUUID().toString(), 
                Integer.toString(status.value()), 
                title, 
                ex.getMessage()));

        responseDTO.setErrors(errorList);

        return new ResponseEntity<>(responseDTO, status);
    }
}
