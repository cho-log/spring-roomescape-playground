package roomescape.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.common.response.ExceptionResponse;
import roomescape.reservation.exception.InvalidReservationException;
import roomescape.time.exception.InvalidTimeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidReservationException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidReservationException(InvalidReservationException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTimeException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTimeException(InvalidTimeException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> validException(MethodArgumentNotValidException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("Validation Failed : " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
