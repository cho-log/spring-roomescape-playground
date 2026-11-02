package roomescape.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.entity.Reservation;
import roomescape.service.ReservationService;

import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<Reservation> found = reservationService.findAll();
        return ResponseEntity.ok(found.stream().map(ReservationResponse::new).toList());
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        Reservation created = reservationService.create(request.getName(), request.getDate(), request.getTime());

        ReservationResponse response = new ReservationResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", "/reservations/" + created.getId()).body(response);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
