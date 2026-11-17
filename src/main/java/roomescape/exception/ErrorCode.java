package roomescape.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_RESERVATION(HttpStatus.BAD_REQUEST, "유효하지 않은 인자 값입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 아이디의 예약을 찾지 못했습니다."),
    TIME_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 시간의 예약을 찾지 못했습니다.");;


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}