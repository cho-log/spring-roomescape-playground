package roomescape.reservation.service;

import roomescape.reservation.dto.Reservation;

import java.util.List;

public interface ReservationService {
    List<Reservation> getReservations();

    Reservation createReservation(Reservation reservation);

    void deleteReservationById(Long id);
}
