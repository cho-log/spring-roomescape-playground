package roomescape.data.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Reservation {

    private Long id;
    private String name;
    private String date;
    private ReservationTime time;

    public Reservation(Long id, String name, String date, ReservationTime time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }
}
