package roomescape.repository;

import roomescape.model.ReservationDTO;

import java.util.List;

public interface ReservationRepository {
    ReservationDTO reservationAdd(ReservationDTO reservationDTO);

    List<ReservationDTO> findAll();

    void delete(int id);
}
