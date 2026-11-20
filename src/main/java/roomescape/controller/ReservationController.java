package roomescape.controller;

import jakarta.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.dto.ErrorResponse;
import roomescape.dto.ReservationRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import roomescape.dto.ReservationResponse;
import roomescape.exception.NotFoundReservationException;

@RestController
public class ReservationController {
    private final JdbcTemplate jdbcTemplate;
    List<Reservation> reservations = new ArrayList<>();
    private AtomicLong index = new AtomicLong(1);

    private final RowMapper<Reservation> rowMapper = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("date"),
            resultSet.getString("time")
    );

    public ReservationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation reservation = request.toEntity();
        Reservation newReservation = reservation.withId(index.getAndIncrement());
        reservations.add(newReservation);
        return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId())).body(ReservationResponse.from(newReservation));
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations() {
        return findAllReservations().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/reservations/{id}")
    public ReservationResponse getReservation(@PathVariable("id") Long id) {
        return ReservationResponse.from(findReservation(id));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        Reservation reservation = findReservation(id);
        reservations.remove(reservation);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NotFoundReservationException.class)
    public ResponseEntity<Void> handleNotFound(NotFoundReservationException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    private Reservation findReservation(Long id) {
        return reservations.stream()
                .filter(it -> Objects.equals(it.getId(), id))
                .findFirst()
                .orElseThrow(() -> new NotFoundReservationException(id));
    }

    private List<Reservation> findAllReservations() {
        String sql = "select id, name, date, time from reservation";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
