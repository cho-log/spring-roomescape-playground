package roomescape.service;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import roomescape.dao.ReservationDao;
import roomescape.dao.TimeDao;
import roomescape.domain.Reservation;
import roomescape.domain.Time;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.dto.TimeResponse;
import roomescape.exception.NotFoundReservationException;

@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final TimeDao timeDao;

    public ReservationService(ReservationDao reservationDao, TimeDao timeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
    }

    public ReservationResponse create(ReservationRequest request) {
        if (isBlank(request.getName()) || isBlank(request.getDate()) || request.getTimeId() == null) {
            throw new IllegalArgumentException("예약자, 날짜, 시간을 모두 입력해야 합니다.");
        }

        Time time = findTime(request.getTimeId());
        Reservation saved = reservationDao.insert(
                new Reservation(null, request.getName(), request.getDate(), time));

        return toResponse(saved);
    }

    public List<ReservationResponse> findAll() {
        return reservationDao.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        int deleted = reservationDao.deleteById(id);
        if (deleted == 0) {
            throw new NotFoundReservationException("존재하지 않는 예약입니다. id=" + id);
        }
    }

    private Time findTime(Long timeId) {
        try {
            return timeDao.findById(timeId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("존재하지 않는 시간입니다. id=" + timeId);
        }
    }

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(r.getId(), r.getName(), r.getDate(),
                new TimeResponse(r.getTime().getId(), r.getTime().getTime()));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
