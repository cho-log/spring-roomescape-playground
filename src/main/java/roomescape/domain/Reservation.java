package roomescape.domain;

import roomescape.exception.InvalidReservationException;

public class Reservation {
    private final Long id;
    private final String name;
    private final String date;
    private final String time;

    public Reservation(String name, String date, String time) {
        this(null, name, date, time);
    }

    public Reservation(Long id, String name, String date, String time) {
        validate(name);
        validate(date);
        validate(time);
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidReservationException("예약 정보를 입력해야 합니다.");
        }
    }

    public Reservation withId(Long id) {
        return new Reservation(id, name, date, time);
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }

}
