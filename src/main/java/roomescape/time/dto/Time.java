package roomescape.time.dto;

import jakarta.validation.constraints.NotBlank;

public class Time {
    private final Long id;
    @NotBlank(message = "Time Field is Required")
    private String time;

    public Time(Long id) {
        this.id = id;
    }

    public Time(Long id, String time) {
        this.id = id;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public String getTime() {
        return time;
    }
}
