package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.dao.TimeDao;
import roomescape.domain.Time;
import roomescape.dto.TimeRequest;
import roomescape.dto.TimeResponse;

@Service
public class TimeService {

    private final TimeDao timeDao;

    public TimeService(TimeDao timeDao) {
        this.timeDao = timeDao;
    }

    public TimeResponse create(TimeRequest request) {
        Time saved = timeDao.insert(new Time(null, request.time()));
        return new TimeResponse(saved.getId(), saved.getTime());
    }

    public List<TimeResponse> findAll() {
        return timeDao.findAll().stream()
                .map(t -> new TimeResponse(t.getId(), t.getTime()))
                .toList();
    }

    public void delete(Long id) {
        timeDao.deleteById(id);
    }
}
