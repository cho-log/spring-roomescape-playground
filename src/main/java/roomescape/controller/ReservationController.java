package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.exception.ReservationNotFoundException;

@Controller
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();

    private final AtomicLong index = new AtomicLong(1);

    public ReservationController() {
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }


    @GetMapping("/reservations")
    @ResponseBody
    public List<ReservationResponse> getReservations() {
        return reservations.stream().map(ReservationResponse::from).toList();
    }


    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation(index.getAndIncrement(),
                request.name(), request.date(), request.time());
        reservations.add(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(ReservationResponse.from(reservation));
    }


    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        boolean removed = reservations.removeIf(reservation -> reservation.hasId(id));
        if (!removed) {
            throw new ReservationNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleInvalidReservation(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<Void> handleReservationNotFound(ReservationNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

}
