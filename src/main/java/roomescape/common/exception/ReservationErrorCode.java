package roomescape.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {

    NO_DELETE_RESERVATION_FOUND(HttpStatus.BAD_REQUEST, "삭제할 예약이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
