package roomescape.reservation.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.time.dto.Time;

public class Reservation {

    private Long id;
    @NotBlank(message = "Name Field is Required")
    private String name;
    @NotBlank(message = "Date Field is Required")
    private String date;
    @NotNull(message = "Time Field is Required")
    private Time time;

    public Reservation() {

    }

    public Reservation(Long id, String name, String date, Time time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public static Reservation toEntity(Long id, String reservationName, String reservationDate, Time reservationTime) {
        return new Reservation(id, reservationName, reservationDate, reservationTime);
    }
}
