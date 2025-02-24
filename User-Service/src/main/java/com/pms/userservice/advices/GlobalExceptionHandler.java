package com.pms.userservice.advices;


import com.pms.userservice.exceptions.ResourceAlreadyExist;
import com.pms.userservice.exceptions.ResourceNotFound;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<APIResponse<?>> handleResourceNotFoundException(ResourceNotFound ex) {
        APIError apiError = APIError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(apiError));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<?>> handleInvalidMethodArgument(MethodArgumentNotValidException ex) {
        // Extract only the default messages from the validation errors
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage) // Get only the default validation message
                .collect(Collectors.toList());

        APIError apiError = APIError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(String.join(", ", errors)) // Join messages into a single string
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(apiError));
    }

    @ExceptionHandler(ResourceAlreadyExist.class)
    public ResponseEntity<APIResponse<?>> handlerConflictExceptions(ResourceAlreadyExist ex) {
        APIError apiError = APIError.builder()
                .status(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse<>(apiError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<?>> handlerInvalidMethodArgument(Exception ex) {
        APIError apiError = APIError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(apiError));
    }


}
