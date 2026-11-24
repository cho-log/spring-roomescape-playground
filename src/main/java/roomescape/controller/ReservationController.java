package roomescape.controller;

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
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDetails;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.ReservationService;

@Controller
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }


    @GetMapping("/reservations")
    @ResponseBody
    public List<ReservationResponse> getReservations() {
        return reservationService.findAll().stream().map(ReservationResponse::from).toList();
    }


    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationDetails details = new ReservationDetails(request.name(), request.date(), request.time());
        Reservation reservation = reservationService.create(details);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(ReservationResponse.from(reservation));
    }


    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
