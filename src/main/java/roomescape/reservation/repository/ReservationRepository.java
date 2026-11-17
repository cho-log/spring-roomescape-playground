package roomescape.reservation.repository;

import roomescape.reservation.dto.Reservation;
import roomescape.time.dto.Time;

import java.util.List;

public interface ReservationRepository {
    List<Reservation> getAllReservations();

    Long createReservation(Reservation reservation, Time existingTime);

    Integer countReservation(Long id);

    boolean existsReservationById(Long id);

    void deleteReservationById(Long id);
}
