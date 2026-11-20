package roomescape.repository;

import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.exception.ReservationNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private long nextReservationId = 1;

    public synchronized List<Reservation> findAll() {
        return List.copyOf(reservations);
    }

    public synchronized Reservation save(String name, LocalDate date, LocalTime time) {
        Reservation createdReservation = new Reservation(
                nextReservationId++,
                name,
                date,
                time
        );

        reservations.add(createdReservation);
        return createdReservation;
    }

    public synchronized void deleteById(long id) {
        boolean removed = reservations.removeIf(
                reservation -> reservation.hasId(id)
        );

        if (!removed) {
            throw new ReservationNotFoundException(id);
        }
    }
}
