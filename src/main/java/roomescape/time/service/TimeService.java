package roomescape.time.service;

import roomescape.time.dto.Time;

import java.util.List;

public interface TimeService {
    List<Time> getTimes();

    Time createTime(Time time);

    void deleteTimeById(Long id);
}
