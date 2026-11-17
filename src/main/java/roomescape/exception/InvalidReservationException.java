package roomescape.exception;

public class InvalidReservationException extends RuntimeException {
    public InvalidReservationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}