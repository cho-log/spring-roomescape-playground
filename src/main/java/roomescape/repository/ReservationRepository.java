package roomescape.repository;

import static java.util.Collections.unmodifiableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.exception.NotFoundReservationException;

@Repository
public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public List<Reservation> findAll() {
        return unmodifiableList(reservations);
    }

    public Reservation save(String name, LocalDate date, LocalTime time) {
        Reservation reservation = new Reservation(idGenerator.incrementAndGet(), name, date, time);
        reservations.add(reservation);
        return reservation;
    }

    public void deleteById(Long id) {
        boolean removed = reservations.removeIf(reservation -> reservation.getId().equals(id));
        if (!removed) {
            throw new NotFoundReservationException("예약 ID " + id + "인 예약을 찾을 수 없습니다.");
        }
    }
}
