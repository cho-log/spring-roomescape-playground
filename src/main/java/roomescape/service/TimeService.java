package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.dao.TimeDao;
import roomescape.entity.ReservationTime;
import roomescape.exception.NotFoundException;

import java.time.LocalTime;
import java.util.List;

@Service
public class TimeService {

    private final TimeDao timeDao;

    public TimeService(TimeDao timeDao) {
        this.timeDao = timeDao;
    }

    public List<ReservationTime> findAll() {
        return timeDao.findAll();
    }

    public ReservationTime create(LocalTime time) {
        ReservationTime validateTime = new ReservationTime(null, time);
        return timeDao.save(validateTime);
    }

    public void delete(long id) {
        int deleted = timeDao.deleteById(id);
        if (deleted == 0) {
            throw new NotFoundException("시간을 찾을 수 없습니다. id = " + id);
        }
    }
}
