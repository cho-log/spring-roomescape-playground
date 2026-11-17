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
import roomescape.data.dto.ReservationRequest;
import roomescape.data.dto.ReservationResponse;
import roomescape.data.service.ReservationService;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(value = "/reservation")
    public String createReservationForm() {
        return "new-reservation";
    }

    @GetMapping(value = "/reservations")
    @ResponseBody
    public List<ReservationResponse> getReservations() {
        return reservationService.getReservations();
    }

    @PostMapping(value = "/reservations")
    @ResponseBody
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.createReservation(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.getId())).body(reservationResponse);
    }

    @DeleteMapping(value = "/reservations/{deletedReservationId}")
    @ResponseBody
    public ResponseEntity<?> deleteReservation(@PathVariable Long deletedReservationId) {
        reservationService.deleteReservation(deletedReservationId);
        return ResponseEntity.noContent().build();
    }
}