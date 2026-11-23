package roomescape.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dao.ReservationDao;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;

import java.net.URI;
import java.util.List;
import roomescape.dto.ReservationResponse;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.NotFoundReservationException;

@RestController
public class ReservationController {
    private final ReservationDao reservationDao;

    public ReservationController(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation reservation = request.toEntity();
        if (reservationDao.existsByDateAndTime(reservation.getDate(), reservation.getTime())) {
            throw new DuplicateReservationException();
        }
        Long id = reservationDao.save(reservation);
        Reservation newReservation = reservation.withId(id);
        return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId())).body(ReservationResponse.from(newReservation));
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations() {
        return reservationDao.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/reservations/{id}")
    public ReservationResponse getReservation(@PathVariable("id") Long id) {
        Reservation reservation = reservationDao.findById(id)
                .orElseThrow(() -> new NotFoundReservationException(id));
        return ReservationResponse.from(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        if (reservationDao.deleteById(id) == 0) {
            throw new NotFoundReservationException(id);
        }
        return ResponseEntity.noContent().build();
    }

}
