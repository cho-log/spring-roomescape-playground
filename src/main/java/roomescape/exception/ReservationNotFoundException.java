package roomescape.exception;

public class ReservationNotFoundException extends RuntimeException{
    public ReservationNotFoundException(long id) {
        super("존재하지 않는 예약입니다. id: " + id);
    }
}
