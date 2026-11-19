package roomescape.dto;

import jakarta.validation.constraints.NotBlank;
import roomescape.domain.Reservation;

public record ReservationRequest(
        @NotBlank(message = "이름을 입력해야 합니다.") String name,
        @NotBlank(message = "날짜를 입력해야 합니다.") String date,
        @NotBlank(message = "시간을 입력해야 합니다.") String time) {

    public Reservation toEntity() {
        return new Reservation(name, date, time);
    }
}
