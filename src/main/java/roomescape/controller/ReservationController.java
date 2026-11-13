package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.repository.ReservationRepository;

import java.net.URI;
import java.util.List;

@RestController
public class ReservationController {
    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservations")
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(
            @RequestBody ReservationRequest reservationRequest
    ) {
        validateReservationRequest(reservationRequest);

        Reservation createdReservation = reservationRepository.save(
                reservationRequest.name(),
                reservationRequest.date(),
                reservationRequest.time()
        );

        return ResponseEntity.created(
                URI.create("/reservations/" + createdReservation.getId())
        ).body(createdReservation);
    }

    private void validateReservationRequest(ReservationRequest request) {
        if (request.name() == null || request.name().isBlank()
                || request.date() == null || request.time() == null) {
            throw new IllegalArgumentException("필수 예약 정보가 비어있습니다.");
        }
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") long id) {
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
