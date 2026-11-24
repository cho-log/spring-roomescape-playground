package roomescape.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDetails;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;
import roomescape.repository.ReservationRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public ReservationService(ReservationRepository reservationRepository, Clock clock) {
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation create(ReservationDetails details) {
        details.validateBookableAt(LocalDateTime.now(clock));
        return reservationRepository.save(details);
    }

    public void deleteById(Long id) {
        if (!reservationRepository.deleteById(id)) {
            throw new ReservationException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }
}
