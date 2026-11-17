package roomescape.reservation.service;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dto.Reservation;
import roomescape.reservation.exception.InvalidReservationException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.time.dto.Time;
import roomescape.time.exception.InvalidTimeException;
import roomescape.time.repository.TimeRepository;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TimeRepository timeRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, TimeRepository timeRepository) {
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
    }

    @Override
    public List<Reservation> getReservations() {
        return reservationRepository.getAllReservations();
    }

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        String reservationName = reservation.getName();
        String reservationDate = reservation.getDate();
        Time reservationTime = reservation.getTime();
        SqlRowSet rowSet = timeRepository.getTimeRowSet(reservationTime);
        /*  id에 해당하는 Time 객체가 여러 개라면 Exception 발생 */
        if (!rowSet.next()) {
            throw new InvalidTimeException("Invalid Time ID: " + reservationTime.getId());
        }
        Time existingTime = new Time(rowSet.getLong("id"), rowSet.getString("time"));
        Long id = reservationRepository.createReservation(reservation, existingTime);
        return Reservation.toEntity(id, reservationName, reservationDate, existingTime);
    }

    @Override
    @Transactional
    public void deleteReservationById(Long id) {
        Integer count = reservationRepository.countReservation(id);
        if (count == 0) {
            throw new InvalidReservationException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteReservationById(id);
    }
}
