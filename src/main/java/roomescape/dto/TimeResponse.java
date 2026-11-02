package roomescape.dto;

import roomescape.entity.ReservationTime;

import java.time.LocalTime;

public class TimeResponse {
    private Long id;
    private LocalTime time;

    public TimeResponse(Long id, LocalTime time) {
        this.id = id;
        this.time = time;
    }

    public TimeResponse(ReservationTime time) {
        this.id = time.getId();
        this.time = time.getTime();
    }

    public Long getId() {
        return id;
    }

    public LocalTime getTime() {
        return time;
    }
}

