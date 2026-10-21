package roomescape.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestReservationException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestReservationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundReservationException.class)
    public ResponseEntity<String> handleNotFound(NotFoundReservationException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
