package roomescape.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.NotFoundReservationException;
import roomescape.model.Reservation;

@Repository
public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong index = new AtomicLong(1);

    public Reservation save(Reservation reservation) {
        boolean isDuplicated = reservations.stream()
                .anyMatch(r -> r.getDate().equals(reservation.getDate())
                        && r.getTime().equals(reservation.getTime()));

        if (isDuplicated) {
            throw new DuplicateReservationException("이미 예약된 날짜와 시간입니다.");
        }

        Reservation savedReservation = new Reservation(
                index.getAndIncrement(),
                reservation.getName(),
                reservation.getDate(),
                reservation.getTime()
        );
        reservations.add(savedReservation);
        return savedReservation;
    }

    public List<Reservation> findAll() {
        return reservations;
    }

    public void deleteById(long id) {
        Reservation reservation = reservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundReservationException("예약이 없습니다."));
        reservations.remove(reservation);
    }
}
