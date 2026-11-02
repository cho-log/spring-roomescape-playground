package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Time;
import roomescape.repository.TimeRepository;

import java.util.List;

@Service
public class TimeService {

    private final TimeRepository timeRepository;

    public TimeService(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    public List<Time> getTimes() {
        return timeRepository.findAll();
    }

    public Time addTime(String value) {
        return timeRepository.save(new Time(null, value));
    }

    public void deleteTime(long id) {
        timeRepository.deleteById(id);
    }
}