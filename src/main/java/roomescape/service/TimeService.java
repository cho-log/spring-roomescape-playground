package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.TimeDao;
import roomescape.model.Time;

@Service
public class TimeService {

    private final TimeDao timeDao;

    public TimeService(TimeDao timeDao) {
        this.timeDao = timeDao;
    }

    public Time create(String value) {
        return timeDao.save(new Time(null, value));
    }

    public List<Time> findAll() {
        return timeDao.findAll();
    }

    public void delete(Long id) {
        timeDao.delete(id);
    }
}
