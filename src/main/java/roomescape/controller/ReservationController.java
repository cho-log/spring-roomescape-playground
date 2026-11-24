package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.ReservationDao;
import roomescape.TimeDao;
import roomescape.dto.CreateReservationRequest;
import roomescape.exception.InvalidReservationTimeException;
import roomescape.exception.ReservationNotFoundException;
import roomescape.model.Reservation;
import roomescape.model.Time;

@Controller
public class ReservationController {

    private final ReservationDao reservationDao;
    private final TimeDao timeDao;

    public ReservationController(ReservationDao reservationDao, TimeDao timeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
    }

    @GetMapping("/reservation")
    public String reservationPage() {
        return "new-reservation";
    }

    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> getReservations() {
        return reservationDao.findAll();
    }

    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Reservation> createReservation(
            @Valid @RequestBody CreateReservationRequest request
    ) {
        Time time = timeDao.findById(request.timeId())
                .orElseThrow(() -> new InvalidReservationTimeException(request.timeId()));

        Reservation saved = reservationDao.save(
                new Reservation(request.name(), request.date(), time));

        return ResponseEntity.created(URI.create("/reservations/" + saved.getId()))
                .body(saved);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        if (reservationDao.delete(id) == 0) {
            throw new ReservationNotFoundException("존재하지 않는 예약입니다. id=" + id);
        }

        return ResponseEntity.noContent().build();
    }
}