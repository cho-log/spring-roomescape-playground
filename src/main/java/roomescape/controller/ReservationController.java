package roomescape.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.ReservationDto;
import roomescape.exception.NotFoundReservationException;
import roomescape.model.Reservation;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong index = new AtomicLong(1);

    @GetMapping
    public List<Reservation> getReservations() {
        return reservations;
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody ReservationDto reservationDto) {
        Reservation reservation = new Reservation(
                index.getAndIncrement(),
                reservationDto.getName(),
                reservationDto.getDate(),
                reservationDto.getTime());

        reservations.add(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        boolean removed = reservations.removeIf(r -> r.getId() == id);
        if (!removed) {
            throw new NotFoundReservationException();
        }
        return ResponseEntity.noContent().build();
    }


}
