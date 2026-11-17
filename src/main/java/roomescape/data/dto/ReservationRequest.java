package roomescape.data.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class ReservationRequest {

    @NotEmpty
    private String date;
    @NotEmpty
    private String name;
    private long time;
}
