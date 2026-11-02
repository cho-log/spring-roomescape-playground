package roomescape.dto;

import java.time.LocalDate;

public class ReservationRequest {
    private String name;
    private LocalDate date;
    private Long time;

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getTime() {
        return time;
    }
}
