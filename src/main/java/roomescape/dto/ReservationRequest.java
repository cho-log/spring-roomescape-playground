package roomescape.dto;

import roomescape.model.Reservation;

public record ReservationRequest(String name, String date, String time) {
    public Reservation toEntity(long id) {
        return new Reservation(id, name, date, time);
    }
}
