package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.repository.ReservationRepository;
import roomescape.dto.ReservationResponse;

@RestController
public class ReservationController {
    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations() {
        return ReservationResponse.fromList(reservationRepository.findAll());
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation(null, request.name(), request.date(), request.time());
        Reservation saved = reservationRepository.save(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + saved.getId()))
                .body(ReservationResponse.from(saved));
    }


    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
