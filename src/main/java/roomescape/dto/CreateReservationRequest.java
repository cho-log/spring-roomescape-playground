package roomescape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateReservationRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 255) String date,
        @JsonProperty("time") @NotNull @Positive Long timeId
) {
}

