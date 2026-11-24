package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Reservation {
    private final Long id;
    private final ReservationDetails details;

    public Reservation(Long id, String name, String date, String time) {
        this(id, new ReservationDetails(name, date, time));
    }

    public Reservation(Long id, ReservationDetails details) {
        if (id == null) {
            throw new IllegalArgumentException("예약 id는 필수입니다.");
        }
        this.id = id;
        this.details = Objects.requireNonNull(details, "예약 내용은 필수입니다.");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return details.getName();
    }

    public LocalDate getDate() {
        return details.getDate();
    }

    public LocalTime getTime() {
        return details.getTime();
    }
}
