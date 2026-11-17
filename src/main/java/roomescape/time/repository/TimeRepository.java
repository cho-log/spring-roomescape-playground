package roomescape.time.repository;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import roomescape.time.dto.Time;

import java.util.List;

public interface TimeRepository {
    SqlRowSet getTimeRowSet(Time reservationTime);

    List<Time> getAllTimes();

    Long createTime(Time time);

    Integer countTimeById(Long id);

    void deleteTimeById(Long id);
}
