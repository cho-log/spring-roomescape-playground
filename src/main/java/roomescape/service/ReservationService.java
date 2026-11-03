package roomescape.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import roomescape.dao.ReservationDao;
import roomescape.dao.TimeDao;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.exception.ConflictException;
import roomescape.exception.InvalidRequestException;
import roomescape.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final TimeDao timeDao;

    public ReservationService(ReservationDao reservationDao, TimeDao timeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
    }

    public List<Reservation> findAll() {
        return reservationDao.findAll();
    }

    public Reservation create(String name, LocalDate date, Long timeId) {
        if (timeId == null) {
            throw new InvalidRequestException("시간을 선택해주세요.");
        }
        ReservationTime reservationTime = timeDao.findById(timeId)
                .orElseThrow(() -> new NotFoundException("시간을 찾을 수 없습니다. id = " + timeId));

        Reservation validatedReservation = new Reservation(null, name, date, reservationTime);
        try {
            return reservationDao.save(validatedReservation);
        } catch (DuplicateKeyException e) {
            throw new ConflictException("이미 예약된 날짜와 시간입니다.");
        }
    }

    public void delete(long id) {
        int deleted = reservationDao.deleteById(id);
        if (deleted == 0) {
            throw new NotFoundException("예약을 찾을 수 없습니다. id = " + id);
        }
    }
}
