package roomescape.domain.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Time {

    @NotNull
    private LocalTime time;
    @NotNull
    private Long id;

    public Time(LocalTime time) {
        this.time = time;
    }

    public Time(String time) {
        this.time = LocalTime.parse(time);
    }

    public Time(Long id, String time) {
        this.id = id;
        this.time = LocalTime.parse(time);
    }
}
