package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;

public class ReservationDetails {
    private final String name;
    private final LocalDate date;
    private final LocalTime time;

    public ReservationDetails(String name, String date, String time) {
        validateRequired(name, ErrorCode.MISSING_NAME);
        validateRequired(date, ErrorCode.MISSING_DATE);
        validateRequired(time, ErrorCode.MISSING_TIME);
        if (name.length() > 255) {
            throw new ReservationException(ErrorCode.NAME_TOO_LONG);
        }
        this.name = name;
        this.date = parseDate(date);
        this.time = parseTime(time);
    }

    private void validateRequired(String value, ErrorCode errorCode) {
        if (value == null || value.isBlank()) {
            throw new ReservationException(errorCode);
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new ReservationException(ErrorCode.INVALID_DATE);
        }
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException exception) {
            throw new ReservationException(ErrorCode.INVALID_TIME);
        }
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
