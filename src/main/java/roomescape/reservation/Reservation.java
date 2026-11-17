package roomescape.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import roomescape.time.Time;

@Data
@AllArgsConstructor
public class Reservation {
  private Long id;
  private final String name;
  private final String date;
  private final Time time;
}
