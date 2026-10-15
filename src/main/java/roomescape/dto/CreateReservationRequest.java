package roomescape.dto;

import jakarta.validation.constraints.NotBlank;


public record CreateReservationRequest (
    @NotBlank String name,
    @NotBlank String date,
    @NotBlank String time
){}
