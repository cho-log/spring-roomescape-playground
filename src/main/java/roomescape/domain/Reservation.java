package roomescape.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class Reservation {
    private Long id;
    private String name;
    private LocalDate date;
    private LocalTime time;

}