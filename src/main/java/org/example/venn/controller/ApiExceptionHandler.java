package org.example.venn.controller;

import org.example.venn.domain.validation.DomainValidationException;
import org.example.venn.service.ConcurrentLoadConflictException;
import org.example.venn.service.DuplicateLoadConflictException;
import org.example.venn.service.InvalidLoadAttemptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(InvalidLoadAttemptException.class)
    public ProblemDetail handleInvalidLoadAttempt(InvalidLoadAttemptException exception) {
        final var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setType(URI.create("https://venn.example/errors/invalid-load-attempt"));
        problem.setTitle("Invalid load attempt");
        return problem;
    }

    @ExceptionHandler(ConcurrentLoadConflictException.class)
    public ProblemDetail handleConcurrentLoadConflict(ConcurrentLoadConflictException exception) {
        final var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage());
        problem.setType(URI.create("https://venn.example/errors/concurrent-load-conflict"));
        problem.setTitle("Concurrent load conflict");
        return problem;
    }

    @ExceptionHandler(DuplicateLoadConflictException.class)
    public ProblemDetail handleDuplicateLoadConflict(DuplicateLoadConflictException exception) {
        final var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage());
        problem.setType(URI.create("https://venn.example/errors/duplicate-load-attempt"));
        problem.setTitle("Duplicate load attempt");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
        final var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setType(URI.create("https://venn.example/errors/invalid-request"));
        problem.setTitle("Invalid request");
        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        final var problem = exception.getBody();
        problem.setTitle("Validation failed");
        problem.setProperty(
                "errors",
                exception.getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toList());
        return handleExceptionInternal(exception, problem, headers, status, request);
    }

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleDomainValidation(DomainValidationException exception) {
        logger.error("Domain validation failed", exception);
        return internalServerError();
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception) {
        logger.error("Unhandled exception", exception);
        return internalServerError();
    }

    private ProblemDetail internalServerError() {
        final var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setType(URI.create("https://venn.example/errors/internal-server-error"));
        problem.setTitle("Internal server error");
        problem.setDetail("An unexpected error occurred. Please contact support.");
        return problem;
    }
}
