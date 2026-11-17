package roomescape.data.repository.repositoryInterface;

import java.util.List;
import roomescape.data.dto.ReservationRequest;
import roomescape.data.entity.Reservation;

public interface ReservationRepository {

    public List<Reservation> findAll();

    public Reservation save(ReservationRequest reservationRequest);

    public void deleteById(Long id);
}
