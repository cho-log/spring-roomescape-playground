package roomescape.exception;

public class DuplicateReservationException extends RuntimeException {
    public DuplicateReservationException() {
        super("이미 예약된 시간입니다.");
    }
}
