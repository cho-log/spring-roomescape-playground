package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong nextReservationId = new AtomicLong(1);

    @GetMapping("/reservation")
    public String showReservationPage() {
        return "reservation";
    }

    @ResponseBody
    @GetMapping("/reservations")
    public List<Reservation> getReservations() {
        return reservations;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationRequest) {
        Reservation createdReservation = Reservation.toEntity(reservationRequest, nextReservationId.getAndIncrement());
        reservations.add(createdReservation);
        return ResponseEntity.created(URI.create("/reservations/" + createdReservation.getId())).body(createdReservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") long id) {
        Reservation reservation = reservations.stream()
                               .filter(it -> it.hasId(id))
                               .findFirst()
                               .orElseThrow(RuntimeException::new);

        reservations.remove(reservation);

        return ResponseEntity.noContent().build();
    }
}
