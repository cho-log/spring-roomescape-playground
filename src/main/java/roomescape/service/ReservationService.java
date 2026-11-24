package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.ReservationDao;
import roomescape.TimeDao;
import roomescape.exception.InvalidReservationTimeException;
import roomescape.exception.ReservationNotFoundException;
import roomescape.model.Reservation;
import roomescape.model.Time;

@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final TimeDao timeDao;

    public ReservationService(ReservationDao reservationDao, TimeDao timeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
    }

    public Reservation create(String name, String date, Long timeId) {
        Time time = timeDao.findById(timeId)
                .orElseThrow(() -> new InvalidReservationTimeException(timeId));
        return reservationDao.save(new Reservation(name, date, time));
    }

    public List<Reservation> findAll() {
        return reservationDao.findAll();
    }

    public void delete(Long id) {
        if (reservationDao.delete(id) == 0) {
            throw new ReservationNotFoundException("존재하지 않는 예약입니다. id=" + id);
        }
    }
}