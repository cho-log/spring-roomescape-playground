package roomescape.exception;

public class InvalidReservationTimeException extends RuntimeException {
    public InvalidReservationTimeException(Long timeId) {
        super("등록되지 않은 예약 시간입니다. id="+timeId);
    }
}
