package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.exception.InvalidReservationFormException;
import roomescape.exception.NotFoundReservationException;

@ControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(NotFoundReservationException.class)
    public ResponseEntity<String> handleNotFound(NotFoundReservationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidReservationFormException.class)
    public ResponseEntity<String> handleInvalidForm(InvalidReservationFormException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
