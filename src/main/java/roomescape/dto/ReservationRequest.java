package roomescape.dto;

import roomescape.domain.Reservation;

public class ReservationRequest {
    private String name;
    private String date;
    private String time;

    public ReservationRequest() {}

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Reservation toEntity(long id) {
        return new Reservation(id, getName(), getDate(), getTime());
    }
}
