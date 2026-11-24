package roomescape.exception;

public class BadRequestReservationException extends RuntimeException {

    public BadRequestReservationException() {
        super("잘못된 예약 요청입니다.");
    }
}
