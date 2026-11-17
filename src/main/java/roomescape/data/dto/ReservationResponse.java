package roomescape.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import roomescape.data.entity.Reservation;
import roomescape.data.entity.ReservationTime;

@AllArgsConstructor
@Builder
@Getter
public class ReservationResponse {

    private long id;
    private String name;
    private String date;
    private ReservationTime time;

    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .date(reservation.getDate())
                .name(reservation.getName())
                .time(reservation.getTime())
                .build();
    }
}
