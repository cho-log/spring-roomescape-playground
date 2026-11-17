package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;

@RestControllerAdvice
public class ReservationExceptionHandler {
    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<Void> handleReservationException(ReservationException exception) {
        if (exception.getErrorCode() == ErrorCode.RESERVATION_NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
