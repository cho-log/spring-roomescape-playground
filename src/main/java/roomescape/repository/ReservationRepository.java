package roomescape.repository;

import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong nextReservationId = new AtomicLong(1);

    public List<Reservation> findAll() {
        return List.copyOf(reservations);
    }

    public Reservation save(String name, LocalDate date, LocalTime time) {
        Reservation createdReservation = new Reservation(
                nextReservationId.getAndIncrement(),
                name,
                date,
                time
        );

        reservations.add(createdReservation);
        return createdReservation;
    }

    public void deleteById(long id) {
        Reservation reservation = reservations.stream()
                                              .filter(it -> it.hasId(id))
                                              .findFirst()
                                              .orElseThrow(RuntimeException::new);

        reservations.remove(reservation);
    }
}
