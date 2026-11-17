package roomescape.exception;

public class NotFoundReservationException extends RuntimeException {
    public NotFoundReservationException() {
        super("존재하지 않는 예약입니다.");
    }
}
