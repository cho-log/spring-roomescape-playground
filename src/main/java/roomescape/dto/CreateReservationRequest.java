package roomescape.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReservationRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank String date,
        @NotBlank String time
) {
}

