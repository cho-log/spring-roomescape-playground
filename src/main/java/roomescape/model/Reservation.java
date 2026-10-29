package roomescape.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor



public class Reservation {
    private Long id;
    private String name;
    private String date;
    private String time;



}
