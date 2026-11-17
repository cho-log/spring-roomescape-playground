package roomescape.time.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.time.dto.Time;
import roomescape.time.exception.InvalidTimeException;
import roomescape.time.repository.TimeRepository;

import java.util.List;

@Service
public class TimeServiceImpl implements TimeService {
    private final TimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public TimeServiceImpl(TimeRepository timeRepository, ReservationRepository reservationRepository) {
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Time> getTimes() {
        return timeRepository.getAllTimes();
    }

    @Override
    @Transactional
    public Time createTime(Time time) {
        Long id = timeRepository.createTime(time);
        return new Time(id, time.getTime());
    }

    @Override
    @Transactional
    public void deleteTimeById(Long id) {
        if (timeRepository.countTimeById(id) == 0) {
            throw new InvalidTimeException("Reservation Time not found with id: " + id);
        } else if (reservationRepository.existsReservationById(id)) {
            throw new InvalidTimeException("Cannot delete Time. It is mapped to a Reservation.");
        } else {
            timeRepository.deleteTimeById(id);
        }
    }
}
