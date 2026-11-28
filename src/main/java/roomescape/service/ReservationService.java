package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.exception.PastReservationException;
import roomescape.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(String name, LocalDate date, LocalTime time) {
        Reservation reservation = Reservation.create(name, date, time);

        validateReservationDateTime(date, time);

        return reservationRepository.save(reservation);
    }

    private void validateReservationDateTime(LocalDate date, LocalTime time) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time);
        LocalDateTime now = LocalDateTime.now();

        if (reservationDateTime.isBefore(now)) {
            throw new PastReservationException(reservationDateTime);
        }
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }
}
