package roomescape.exception;

public class NotFoundReservationException extends RuntimeException {
    public NotFoundReservationException(Long id) {
        super(id + "번 예약을 찾을 수 없습니다.");
    }
}
