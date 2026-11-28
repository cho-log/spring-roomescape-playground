package roomescape.exception;

import java.time.LocalDateTime;

public class PastReservationException extends RuntimeException{
    public PastReservationException(LocalDateTime reservationDateTime) {
        super("과거 예약 시도: " + reservationDateTime);
    }
}
