package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.exception.PastReservationException;
import roomescape.repository.ReservationRepository;

import java.time.LocalDateTime;
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

    public Reservation createReservation(ReservationRequest request) {
        validateReservationDateTime(request);

        return reservationRepository.save(
                request.name(),
                request.date(),
                request.time()
        );
    }

    private void validateReservationDateTime(ReservationRequest request) {
        LocalDateTime reservationDateTime = LocalDateTime.of(request.date(), request.time());
        LocalDateTime now = LocalDateTime.now();

        if (reservationDateTime.isBefore(now)) {
            throw new PastReservationException(reservationDateTime);
        }
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }
}
