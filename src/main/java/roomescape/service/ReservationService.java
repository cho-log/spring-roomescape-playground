package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.exception.NotFoundReservationException;
import roomescape.model.Reservation;
import roomescape.repository.ReservationRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.selectAll();
    }

    public Reservation create(String name, String date, String time) {
        return reservationRepository.insert(name, date, time);
    }

    public void delete(long id) {
        int removed = reservationRepository.delete(id);
        if (removed == 0) {
            throw new NotFoundReservationException();
        }
    }
}
