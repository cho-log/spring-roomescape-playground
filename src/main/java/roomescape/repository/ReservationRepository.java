package roomescape.repository;

import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class ReservationRepository implements ReservationRepositoryImpl{
    private final Map<Long, Reservation> reservations = new HashMap<>();
    private final AtomicLong atomicLong = new AtomicLong(0);

    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("예약 정보가 없습니다.");
        }

        if (reservation.getId() != null && reservations.containsKey(reservation.getId())) {
            throw new IllegalArgumentException("이미 등록된 예약입니다.");
        }
        reservation.setId(atomicLong.incrementAndGet());
        reservations.put(reservation.getId(), reservation);

        return reservation;
    }

    @Override
    public Reservation findById(Long id) {
        return reservations.get(id);
    }

    @Override
    public void deleteById(Long id) {
        reservations.remove(id);
    }
}
