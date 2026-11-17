package roomescape.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.dto.Reservation;
import roomescape.reservation.service.ReservationService;

import java.net.URI;
import java.util.*;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> getReservations() {
        return reservationService.getReservations();
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody @Valid Reservation reservation) {
        Reservation newReservation = reservationService.createReservation(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId())).body(newReservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
