package roomescape.data.dao.daoInterface;

import java.util.List;
import roomescape.data.dto.ReservationTimeRequest;
import roomescape.data.entity.ReservationTime;

public interface ReservationTimeDao {

    public ReservationTime save(ReservationTimeRequest reservationTimeRequest);
    public List<ReservationTime> findAll();
    public ReservationTime findById(long id);
    public void deleteById(long id);
}
