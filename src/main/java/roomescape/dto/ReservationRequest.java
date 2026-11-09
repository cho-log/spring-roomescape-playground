package roomescape.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.model.Reservation;

public record ReservationRequest(String name, LocalDate date, LocalTime time) {
    public Reservation toEntity() {
        return new Reservation(name, date, time);
    }
}
