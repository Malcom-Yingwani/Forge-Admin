package za.co.heritagebaptist.forge.content.admin;
import java.util.List;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class ContentExceptionHandler {
    @ExceptionHandler(Invalid.class)
    public ResponseEntity<ProblemDetail> invalid(Invalid exception) {
        var problem=ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(exception.status),exception.getMessage());
        problem.setProperty("errors",List.of(Map.of("field",exception.field,"message",exception.getMessage())));
        return ResponseEntity.status(exception.status).body(problem);
    }
    public static class Invalid extends RuntimeException {
        public final String field; public final int status;
        public Invalid(String field,String message) { this(field,message,422); }
        public Invalid(String field,String message,int status) { super(message); this.field=field; this.status=status; }
    }
}
