package roomescape.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import roomescape.dto.ErrorResponse;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;

@RestControllerAdvice
public class ReservationExceptionHandler {
    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ErrorResponse> handleReservationException(ReservationException exception) {
        ErrorCode code = exception.getErrorCode();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (code == ErrorCode.RESERVATION_NOT_FOUND) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(ErrorResponse.from(code));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
        return ResponseEntity.badRequest().body(ErrorResponse.from(ErrorCode.INVALID_REQUEST));
    }
}
