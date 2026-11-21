package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import roomescape.repository.ReservationRepository;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getReservations() {
        return reservationService.findAll();
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody ReservationDto reservationDto) {
        Reservation reservation = reservationService.create(
                reservationDto.getName(),
                reservationDto.getDate(),
                reservationDto.getTime()
        );
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
