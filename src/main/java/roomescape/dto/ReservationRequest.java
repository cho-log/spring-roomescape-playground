package roomescape.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequest(
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        @Size(max = 255, message = "이름은 255자 이하여야 합니다.")
        String name,

        @NotNull(message = "날짜는 비어있을 수 없습니다.")
        LocalDate date,

        @NotNull(message = "시간은 비어있을 수 없습니다.")
        LocalTime time
) {
}
