package roomescape.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DuplicateReservationException extends RuntimeException{
    public DuplicateReservationException(LocalDate date, LocalTime time) {
        super("이미 예약된 시각: " + LocalDateTime.of(date, time));
    }
}
