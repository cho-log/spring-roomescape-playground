package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.dto.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFound(ReservationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ErrorResponse("예약을 찾을 수 없습니다.", Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ErrorResponse("입력값이 올바르지 않습니다.", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ErrorResponse("입력값이 올바르지 않습니다.", Map.of()));
    }

    @ExceptionHandler(PastReservationException.class)
    public ResponseEntity<ErrorResponse> handlePastReservationException(PastReservationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ErrorResponse(
                                     "지난 날짜와 시간으로 예약할 수 없습니다.", Map.of()
                             ));
    }

    @ExceptionHandler(DuplicateReservationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReservationException(DuplicateReservationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(new ErrorResponse(
                                     "이미 예약된 날짜와 시간입니다", Map.of()
                             ));
    }
}
