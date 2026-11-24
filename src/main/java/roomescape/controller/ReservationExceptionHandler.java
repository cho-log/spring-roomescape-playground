package roomescape.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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
    private static final Logger log = LoggerFactory.getLogger(ReservationExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException exception) {
        log.error("예약 데이터 처리 중 오류가 발생했습니다.", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.from(ErrorCode.INTERNAL_ERROR));
    }

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
