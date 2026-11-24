package roomescape.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import roomescape.domain.Reservation;


public record ReservationResponse(
        Long id,
        String name,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                reservation.getTime()
        );
    }

        public static List<ReservationResponse> fromList(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

}
