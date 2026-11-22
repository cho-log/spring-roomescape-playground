package roomescape.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservationDto (
    @NotBlank(message = "이름을 입력해주세요.")
    String name,
    @NotBlank(message = "날짜를 입력해주세요.")
    String date,
    @NotBlank(message = "시간을 입력해주세요.")
    String time
){
}
