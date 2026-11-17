package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.exception.InvalidReservationFormException;
import roomescape.exception.NotFoundReservationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private final List<Reservation> reservations = new ArrayList<>();
    private AtomicLong index = new AtomicLong(0);

    public List<Reservation> getReservations() {
        return reservations;
    }

    public Reservation createReservation(Reservation reservation) {
        validateReservation(reservation);
        long reservationId = index.incrementAndGet();
        reservation.setId(reservationId);
        reservations.add(reservation);
        return reservation;
    }

    public void deleteReservation(long reservationId) {
        Optional<Reservation> reservationOptional = reservations.stream()
                .filter(reservation -> reservation.getId() == reservationId)
                .findFirst();

        if (reservationOptional.isPresent()) {
            reservations.remove(reservationOptional.get());
        } else {
            throw new NotFoundReservationException("예약을 찾을 수 없습니다.");
        }
    }

    private void validateReservation(Reservation reservation) {
        if (reservation.getName() == null || reservation.getDate() == null || reservation.getTime() == null || reservation.getName().isEmpty()) {
            throw new InvalidReservationFormException("필수 정보가 비어있습니다.");
        }

        if (reservation.getDate().isBefore(LocalDate.now())) {
            throw new InvalidReservationFormException("예약 날짜는 오늘보다 이전일 수 없습니다.");
        }
    }
}
