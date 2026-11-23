package roomescape.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import roomescape.domain.Reservation;

@GroupSequence({ReservationRequest.class, ReservationRequest.PatternCheck.class, ReservationRequest.SizeCheck.class})
public record ReservationRequest(
        @NotBlank(message = "이름을 입력해야 합니다.")
        @Pattern(regexp = "[^0-9]*", message = "이름에 숫자를 포함할 수 없습니다.", groups = PatternCheck.class)
        @Size(max = 10, message = "이름은 10자 이내여야 합니다.", groups = SizeCheck.class)
        String name,
        @NotBlank(message = "날짜를 입력해야 합니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.", groups = PatternCheck.class)
        String date,
        @NotBlank(message = "시간을 입력해야 합니다.")
        @Pattern(regexp = "\\d{2}:\\d{2}", message = "시간은 HH:mm 형식이어야 합니다.", groups = PatternCheck.class)
        String time) {

    interface PatternCheck {
    }

    interface SizeCheck {
    }

    public Reservation toEntity() {
        return new Reservation(name, date, time);
    }
}
