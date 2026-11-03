package roomescape.dto;

import roomescape.entity.Reservation;

import java.time.LocalDate;

public class ReservationResponse {
    private Long id;
    private String name;
    private LocalDate date;
    private TimeResponse time;

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.name = reservation.getName();
        this.date = reservation.getDate();
        this.time = new TimeResponse(reservation.getTime());
    }

    public ReservationResponse(Long id, String name, LocalDate date, TimeResponse time) {
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

    public LocalDate getDate() {
        return date;
    }

    public TimeResponse getTime() {
        return time;
    }
}
