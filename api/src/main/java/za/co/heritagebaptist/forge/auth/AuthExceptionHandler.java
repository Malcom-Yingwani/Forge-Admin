package za.co.heritagebaptist.forge.auth;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {
    @ExceptionHandler(AuthFailure.class)
    public ResponseEntity<ProblemDetail> unauthorized() {
        return ResponseEntity.status(401).body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid credentials or expired session"));
    }
    @ExceptionHandler(LoginThrottle.Limited.class)
    public ResponseEntity<ProblemDetail> limited(LoginThrottle.Limited exception) {
        return ResponseEntity.status(429).header("Retry-After", Long.toString(exception.retryAfter))
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage()));
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> conflict() {
        return ResponseEntity.status(409).body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Value already in use or referenced by another record"));
    }
}

