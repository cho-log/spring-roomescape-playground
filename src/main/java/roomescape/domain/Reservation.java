package roomescape.domain;

import roomescape.exception.InvalidReservationException;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private final Long id;
    private final String name;
    private final LocalDate date;
    private final LocalTime time;

    private Reservation(Long id, String name, LocalDate date, LocalTime time) {
        validateFields(name, date, time);

        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    private void validateFields(String name, LocalDate date, LocalTime time) {
        if (name == null || name.isBlank()) {
            throw new InvalidReservationException("이름은 비어있을 수 없습니다.");
        }

        if (name.length() > 255) {
            throw new InvalidReservationException("이름은 255자 이하여야 합니다.");
        }

        if (date == null) {
            throw new InvalidReservationException("날짜는 비어있을 수 없습니다.");
        }

        if (time == null) {
            throw new InvalidReservationException("시간은 비어있을 수 없습니다.");
        }
    }

    public static Reservation create(String name, LocalDate date, LocalTime time) {
        return new Reservation(null, name, date, time);
    }

    public static Reservation restore(long id, String name, LocalDate date, LocalTime time) {
        validateId(id);

        return new Reservation(id, name, date, time);
    }

    private static void validateId(long id) {
        if (id <= 0) {
            throw new InvalidReservationException("예약 ID는 양수여야 합니다.");
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

    public boolean hasId(long id) {
        return this.id == id;
    }
}
