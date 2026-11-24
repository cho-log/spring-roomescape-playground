package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private static final int MAX_NAME_LENGTH = 10;

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final LocalTime time;

    public Reservation(String name, LocalDate date, LocalTime time) {
        this(null, name, date, time);
    }

    public Reservation(Long id, String name, LocalDate date, LocalTime time) {
        validateValues(name, date, time);
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public Reservation withId(Long id) {
        return new Reservation(id, name, date, time);
    }

    private void validateValues(String name, LocalDate date, LocalTime time) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력해야 합니다.");
        }
        if (name.chars().anyMatch(Character::isDigit)) {
            throw new IllegalArgumentException("이름에 숫자를 포함할 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 " + MAX_NAME_LENGTH + "자 이내여야 합니다.");
        }

        if (date == null) {
            throw new IllegalArgumentException("날짜를 입력해야 합니다.");
        }

        if (time == null) {
            throw new IllegalArgumentException("시간을 입력해야 합니다.");
        }
    }

    public Long getId() {
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
