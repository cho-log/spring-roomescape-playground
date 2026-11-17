package roomescape.repository;

import roomescape.domain.Reservation;

import java.util.List;

public interface ReservationRepositoryImpl {

    List<Reservation> findAll();
    Reservation save(Reservation reservation);
    Reservation findById(Long id);
    void deleteById(Long id);
}
