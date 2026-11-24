package roomescape.model;

import java.util.Objects;
import lombok.Getter;

@Getter
public class Reservation {

    private final Long id;
    private final String name;
    private final String date;
    private final Time time;

    public Reservation(Long id, String name, String date, Time time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = Objects.requireNonNull(time, "예약에는 시간이 필요합니다.");
    }

    public Reservation(String name, String date, Time time) {
        this(null, name, date, time);
    }
}
