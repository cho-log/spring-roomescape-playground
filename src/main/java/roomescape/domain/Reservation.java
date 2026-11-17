package roomescape.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import roomescape.domain.DTO.ReservationDTO;
import roomescape.domain.value.Date;
import roomescape.domain.value.ID;
import roomescape.domain.value.Name;
import roomescape.domain.value.Time;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {


    private ID id;
    private Name name;
    private Date date;
    private Time time;

    public Reservation(long id, String name, String date, String time) {
        this.id = new ID(id);
        this.name = new Name(name);
        this.date = new Date(date);
        this.time = new Time(time);
    }

    public Reservation(String name, String date, String time) {
        this.name = new Name(name);
        this.date = new Date(date);
        this.time = new Time(time);
    }

    public Reservation(Long newId, String name, LocalDate date, LocalTime time) {
        this.id = new ID(newId);
        this.name = new Name(name);
        this.date = new Date(date);
        this.time = new Time(time);
    }

    public ReservationDTO toDTO() {
        return new ReservationDTO(this.id.getId(), this.name.getName(), this.date.getDate(), this.time.getTime());
    }

    public Long getID() {
        return id.getId();
    }

    public String getName() {
        return name.getName();
    }

    public LocalDate getDate() {
        return date.getDate();
    }

    public LocalTime getTime() {
        return time.getTime();
    }

    public Long getTimeId() {
        return time.getId();
    }
}
