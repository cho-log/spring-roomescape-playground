package roomescape.domain;

import lombok.Data;
import java.time.LocalTime;

@Data
public class Time {
    private Long id;
    private LocalTime time;

    public Time(Long id, String time) {
        this.id = id;
        this.time = LocalTime.parse(time);
    }

    public Time(String time) {
        this.time = LocalTime.parse(time);
    }

    public Time toEntity(Long id) {
        return new Time(id, time.toString());
    }
}
