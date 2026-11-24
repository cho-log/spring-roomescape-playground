package roomescape.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTimeRequest(
        @NotBlank @Size(max = 255) String time
) {
}
