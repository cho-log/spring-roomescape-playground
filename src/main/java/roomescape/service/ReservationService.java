package roomescape.service;

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

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation create(ReservationDetails details) {
        return reservationRepository.save(details);
    }

    public void deleteById(Long id) {
        if (!reservationRepository.deleteById(id)) {
            throw new ReservationException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }
}
