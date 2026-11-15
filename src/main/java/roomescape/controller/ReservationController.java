package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.domain.Time;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.dto.TimeResponse;
import roomescape.exception.NotFoundReservationException;

@RestController
public class ReservationController {

    private final ReservationDao reservationDao;
    private final TimeDao timeDao;

    public ReservationController(ReservationDao reservationDao, TimeDao timeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        int deleted = reservationDao.deleteById(id);
        if (deleted == 0) {
            throw new NotFoundReservationException("존재하지 않는 예약입니다. id=" + id);
        }
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationRequest request) {
        if (isBlank(request.getName()) || isBlank(request.getDate()) || request.getTimeId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Time time = timeDao.findById(request.getTimeId());
        Reservation saved = reservationDao.insert(
                new Reservation(null, request.getName(), request.getDate(), time));

        ReservationResponse response = new ReservationResponse(
                saved.getId(), saved.getName(), saved.getDate(),
                new TimeResponse(time.getId(), time.getTime()));

        return ResponseEntity.created(URI.create("/reservations/" + saved.getId())).body(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> responses = reservationDao.findAll().stream()
                .map(r -> new ReservationResponse(r.getId(), r.getName(), r.getDate(),
                        new TimeResponse(r.getTime().getId(), r.getTime().getTime())))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Void> handleBadRequest() {
        return ResponseEntity.badRequest().build();
    }
}