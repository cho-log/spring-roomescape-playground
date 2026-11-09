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
import roomescape.domain.ReservationDTO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ReservationController {
    List<Reservation> reservations = new ArrayList<>();
    private AtomicLong index = new AtomicLong(1);

    @GetMapping("/reservation")
    public String showReservationPage() {
        return "reservation";
    }

    @ResponseBody
    @GetMapping("/reservations")
    public List<Reservation> getReservations() {
        // reservations.add(new Reservation(1, "red", "2026-11-03", "10:00"));
        // reservations.add(new Reservation(2, "brown", "2026-11-03", "11:00"));
        return reservations;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationDTO reservation) {
        Reservation newReservation = reservation.toEntity(reservation, index.getAndIncrement());
        reservations.add(newReservation);
        return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId())).body(newReservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        Reservation reservation = reservations.stream()
                               .filter(it -> Objects.equals(it.getId(), id))
                               .findFirst()
                               .orElseThrow(RuntimeException::new);

        reservations.remove(reservation);

        return ResponseEntity.noContent().build();
    }

}
