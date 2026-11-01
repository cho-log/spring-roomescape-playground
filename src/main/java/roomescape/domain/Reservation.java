package roomescape.domain;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Reservation {

    private final Long id;
    private final String name;
    private final String date;
    private final Time time;

    public Reservation(Long id, String name, String date, Time time) {
        validate(name, date);

        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    private void validate(String name, String date) {
        if (name == null || name.isBlank() || name.length() > 255
                || date == null || date.isBlank()) {
            throw new IllegalArgumentException("예약 정보를 확인해주세요.");
        }

        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다.");
        }
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

    public Time getTime() {
        return time;
    }
}