package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;
import roomescape.exception.BadRequestReservationException;
import roomescape.exception.NotFoundReservationException;

@Controller
public class ReservationController {

    private List<Reservation> reservations = new ArrayList<>();
    private AtomicLong index = new AtomicLong(1);

    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> reservations() {
        return reservations;
    }

    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Reservation> create(@RequestBody Reservation request) {
        if (isBlank(request.getName()) || isBlank(request.getDate()) || isBlank(request.getTime())) {
            throw new BadRequestReservationException("예약자, 날짜, 시간을 모두 입력해야 합니다.");
        }

        Reservation reservation = new Reservation(
                index.getAndIncrement(),
                request.getName(),
                request.getDate(),
                request.getTime()
        );
        reservations.add(reservation);

        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = reservations.removeIf(reservation -> reservation.getId().equals(id));
        if (!removed) {
            throw new NotFoundReservationException("존재하지 않는 예약입니다. id=" + id);
        }
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
