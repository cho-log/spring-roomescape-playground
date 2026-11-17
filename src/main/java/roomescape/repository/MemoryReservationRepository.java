package roomescape.repository;

import org.springframework.stereotype.Repository;
import roomescape.model.ReservationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemoryReservationRepository implements ReservationRepository {
    private static int id = 0;
    private static final Map<Integer, ReservationDTO> reservationStore = new HashMap<>();

    @Override
    public ReservationDTO reservationAdd(ReservationDTO reservationDTO) {
        reservationDTO.setId(++id);
        reservationStore.put(reservationDTO.getId(), reservationDTO);
        return reservationDTO;
    }

    @Override
    public List<ReservationDTO> findAll() {
        return new ArrayList<>(reservationStore.values());
    }

    @Override
    public void delete(int id) {
        if (reservationStore.containsKey(id)) {
            reservationStore.remove(id);
        }
    }
}
