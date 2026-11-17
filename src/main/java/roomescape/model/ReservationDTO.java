package roomescape.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReservationDTO {
    int id;

    @NotBlank
    String name;

    @NotBlank
    String date;

    @NotBlank
    String time;

    public ReservationDTO(){
    }

    public ReservationDTO(String name, String date, String time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }
}
