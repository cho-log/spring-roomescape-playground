package roomescape.domain;

import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;

public class Reservation {
    private final Long id;
    private final String name;
    private final String date;
    private final String time;

    public Reservation(Long id, String name, String date, String time) {
        if (id == null) {
            throw new IllegalArgumentException("예약 id는 필수입니다.");
        }
        validateRequired(name, ErrorCode.MISSING_NAME);
        validateRequired(date, ErrorCode.MISSING_DATE);
        validateRequired(time, ErrorCode.MISSING_TIME);
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    private void validateRequired(String value, ErrorCode errorCode) {
        if (value == null || value.isBlank()) {
            throw new ReservationException(errorCode);
        }
    }

    public boolean hasId(Long id) {
        return this.id.equals(id);
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
