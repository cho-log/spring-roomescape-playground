package roomescape.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.domain.Time;
import roomescape.exception.NotFoundReservationException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.TimeRepository;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TimeRepository timeRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            TimeRepository timeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
    }

    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    public Reservation addReservation(
            String name,
            String date,
            String timeValue
    ) {
        if (timeValue == null || timeValue.isBlank()) {
            throw new IllegalArgumentException("예약 정보를 확인해주세요.");
        }

        Long timeId;

        try {
            timeId = Long.valueOf(timeValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("시간 ID 형식이 올바르지 않습니다.");
        }

        Time time;

        try {
            time = timeRepository.findById(timeId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("등록되지 않은 시간입니다.");
        }

        return reservationRepository.save(
                new Reservation(null, name, date, time)
        );
    }

    public void deleteReservation(long id) {
        int deletedCount = reservationRepository.deleteById(id);

        if (deletedCount == 0) {
            throw new NotFoundReservationException();
        }
    }
}