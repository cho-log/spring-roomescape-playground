package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.exception.NotFoundReservationException;

@RestController
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();

    private final AtomicLong idGenerator = new AtomicLong(0);

    @GetMapping("/reservations")
    public List<Reservation> getReservations() {
        return reservations;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation(idGenerator.incrementAndGet(), request.name(), request.date(),
                request.time());
        reservations.add(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(reservation);
    }


    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        boolean removed = reservations.removeIf(reservation -> reservation.getId().equals(id));
        if (!removed) {
            throw new NotFoundReservationException("예약 ID " + id + "인 예약을 찾을 수 없습니다.");
        }
        return ResponseEntity.noContent().build();
    }

}
