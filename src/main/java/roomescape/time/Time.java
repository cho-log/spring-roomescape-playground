package roomescape.time;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Time {
  private Long id;
  private String time;

  public Time(Long id) {
    this.id = id;
  }
}
