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

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public List<Reservation> getReservations() {
        return reservationRepository.selectAll();
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody ReservationDto reservationDto) {
        Reservation reservation = reservationRepository.insert(
                reservationDto.getName(),
                reservationDto.getDate(),
                reservationDto.getTime()
        );
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        int removed = reservationRepository.delete(id);
        if (removed == 0) {
            throw new NotFoundReservationException();
        }
        return ResponseEntity.noContent().build();
    }
}
