package roomescape.valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USER_NO_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, "Illegal argument"),
    RESERVATION_NO_FOUND(HttpStatus.BAD_REQUEST, "Reservation not found"),
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "User name is duplicated"),
    TIME_NO_FOUND(HttpStatus.BAD_REQUEST, "Time not found");

    private final HttpStatus httpStatus;
    private final String message;
}
