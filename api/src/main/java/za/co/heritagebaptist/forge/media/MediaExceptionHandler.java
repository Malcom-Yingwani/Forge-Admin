package za.co.heritagebaptist.forge.media;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class MediaExceptionHandler {
    @ExceptionHandler(Rejected.class) public ResponseEntity<ProblemDetail> rejected(Rejected e) { return ResponseEntity.status(e.status).body(ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(e.status),e.getMessage())); }
    @ExceptionHandler({java.io.IOException.class,software.amazon.awssdk.core.exception.SdkException.class})
    public ResponseEntity<ProblemDetail> unavailable() { return ResponseEntity.status(503).body(ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,"Media storage is temporarily unavailable")); }
    @ExceptionHandler({org.springframework.web.multipart.support.MissingServletRequestPartException.class,org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<ProblemDetail> missingFile() { return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Choose a file to upload")); }
    public static class Rejected extends RuntimeException {
        public final int status;
        public Rejected(int status,String message) { super(message);this.status=status; }
    }
}
