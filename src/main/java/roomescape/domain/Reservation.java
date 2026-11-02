package roomescape.domain;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Reservation {
    private static final int MAX_NAME_LENGTH = 255;
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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("예약자 이름을 입력해주세요.");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "예약자 이름은 " + MAX_NAME_LENGTH + "자 이하여야 합니다."
            );
        }

        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("예약 날짜를 입력해주세요.");
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