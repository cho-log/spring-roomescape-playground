package roomescape.entity;

import java.time.LocalTime;

public class ReservationTime {
    private final Long id;
    private final LocalTime time;

    public ReservationTime(Long id, LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("시간을 적어주세요.");
        }
        this.id = id;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public LocalTime getTime() {
        return time;
    }
}
