package roomescape.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.exception.InvalidReservationException;
import roomescape.exception.PastReservationException;

public class Reservation {
    private final long id;
    private final String name;
    private final LocalDate date;
    private final LocalTime time;

    public Reservation(long id, String name, LocalDate date, LocalTime time) {
        validate(name, date, time);
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public Reservation(String name, LocalDate date, LocalTime time) {
        this(0, name, date, time);
    }

    private void validate(String name, LocalDate date, LocalTime time) {
        if (name == null || name.isBlank() || date == null || time == null) {
            throw new InvalidReservationException("이름, 날짜, 시간은 비어 있을 수 없습니다.");
        }

        if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
            throw new PastReservationException("지난 날짜와 시간으로는 예약할 수 없습니다.");
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }
}
