package roomescape.dto;

import jakarta.validation.constraints.NotBlank;

public class ReservationDto {
    @NotBlank(message="이름을 입력해주세요.")
    private final String name;
    @NotBlank(message="날짜를 입력해주세요.")
    private final String date;
    @NotBlank(message="시간을 입력해주세요.")
    private final String time;

    public ReservationDto(String name, String date, String time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
