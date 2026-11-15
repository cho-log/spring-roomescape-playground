package roomescape.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.exception.InvalidReservationException;

public class Reservation {

    private final Long id;
    private final String name;
    private final LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private final LocalTime time;


    public Reservation(Long id, String name, LocalDate date, LocalTime time) {
        validate(name, date, time);
        this.name = name;
        this.date = date;
        this.time = time;
        this.id = id;
    }

    private void validate(String name, LocalDate date, LocalTime time) {
        if (name == null || name.isBlank() || date == null || time == null) {
            throw new InvalidReservationException("예약 정보(name, date, time)는 비어 있을 수 없습니다.");
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
