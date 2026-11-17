package roomescape.dto;

import lombok.Getter;
import lombok.Setter;
import roomescape.exception.BadRequestException;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservationRequest {
    private String name;
    private LocalDate date;
    private LocalTime time;

    public void validate() {
        if (name == null || name.isEmpty()) {
            throw new BadRequestException("예약 이름은 필수 입력값입니다.");
        }

        if (date == null) {
            throw new BadRequestException("예약 날짜는 필수 입력값입니다.");
        }

        if (time == null) {
            throw new BadRequestException("예약 시간은 필수 입력값입니다.");
        }
    }

}
