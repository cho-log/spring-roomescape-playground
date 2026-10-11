package roomescape.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // ReservationNotFoundException이 발생했을 때 404 Not Found를 반환
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(
            ReservationNotFoundException e
    ){
        return ResponseEntity.notFound().build();
    }
}
