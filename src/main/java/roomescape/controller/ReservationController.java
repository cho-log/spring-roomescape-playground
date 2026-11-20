package roomescape.controller;

import jakarta.validation.Valid;
import java.sql.PreparedStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
import java.util.List;
import roomescape.dto.ReservationResponse;
import roomescape.exception.NotFoundReservationException;

@RestController
public class ReservationController {
    private final JdbcTemplate jdbcTemplate;

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
        Long id = saveReservation(reservation);
        Reservation newReservation = reservation.withId(id);
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
        if (removeReservation(id) == 0) {
            throw new NotFoundReservationException(id);
        }
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
        String sql = "select id, name, date, time from reservation where id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundReservationException(id));
    }

    private List<Reservation> findAllReservations() {
        String sql = "select id, name, date, time from reservation";
        return jdbcTemplate.query(sql, rowMapper);
    }

    private Long saveReservation(Reservation reservation) {
        String sql = "insert into reservation (name, date, time) values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, reservation.getName());
            ps.setString(2, reservation.getDate());
            ps.setString(3, reservation.getTime());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private int removeReservation(Long id) {
        String sql = "delete from reservation where id = ?";
        return jdbcTemplate.update(sql, id);
    }

}
