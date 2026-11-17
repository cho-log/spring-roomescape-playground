package roomescape.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import roomescape.DAO.ReservationTimeDAO;
import roomescape.domain.value.Time;

import java.util.List;

@Service
public class ReservationTimeService {
    private final ReservationTimeDAO reservationTimeDAO;

    @Autowired
    public ReservationTimeService(ReservationTimeDAO reservationTimeDAO) {
        this.reservationTimeDAO = reservationTimeDAO;
    }

    public Time createTimeSlot(String time) {
        return reservationTimeDAO.save(time);
    }

    public List<Time> getAllTimeSlots() {
        return reservationTimeDAO.findAll();
    }

    public void deleteTimeSlot(Long id) {
        reservationTimeDAO.deleteById(id);
    }
}
