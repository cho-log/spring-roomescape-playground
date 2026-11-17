package roomescape.reservation;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.common.BadRequestException;

@Controller
@RequiredArgsConstructor
public class ReservationController {
  private final ReservationService reservationService;

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/reservation")
  public String reservation(){
    return "new-reservation";
  }

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> getReservations() {
    List<Reservation> reservations = reservationService.getReservations();
    return ResponseEntity.ok()
        .body(reservations);
  }

  @PostMapping("/reservations")
  public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation)
      throws BadRequestException {
    final boolean isEmpty = reservation.getName().isEmpty() || reservation.getDate().isEmpty();
    if (isEmpty) {
      throw new BadRequestException("필수 정보가 누락되었습니다");
    }

    Long id = reservationService.addReservation(reservation);
    return ResponseEntity.created(URI.create("/reservations/" + id))
        .body(reservation);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<Void> deleteReservation(@PathVariable Long id) throws BadRequestException {
    final boolean deleted = reservationService.deleteReservation(id);
    if (!deleted) {
      throw new BadRequestException("해당하는 예약이 존재하지 않습니다");
    }
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<String> handleException(BadRequestException e) {
    return ResponseEntity.badRequest()
        .body(e.getMessage());
  }
}
