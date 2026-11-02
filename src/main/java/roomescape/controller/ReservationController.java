package roomescape.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.Reservation;
import roomescape.exception.NotFoundReservationException;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


@Controller
public class ReservationController {

    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> getReservations() {
        return jdbcTemplate.query(
                "SELECT id, name, date, time FROM reservation",
                (rs, rowNum) -> new Reservation(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time")
                )
        );

    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> addReservation(
            @RequestBody Reservation request) {
        if (request.getName() == null || request.getName().isBlank() ||
                request.getName().length() > 255 ||
                request.getDate() == null || request.getDate().isBlank() ||
                request.getTime() == null || request.getTime().isBlank()) {

            throw new IllegalArgumentException("필수 인자가 누락되었습니다.");
        }
        try {
            LocalDate.parse(request.getDate());
            LocalTime.parse(request.getTime());

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 또는 시간 형식이 올바르지 않습니다.");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, request.getName());
            statement.setString(2, request.getDate());
            statement.setString(3, request.getTime());
            return statement;
        }, keyHolder);

        Long newId = keyHolder.getKey().longValue();

        Reservation newReservation = new Reservation(
                newId,
                request.getName(),
                request.getDate(),
                request.getTime()
        );

        return ResponseEntity
                .created(URI.create("/reservations/" + newId))
                .body(newReservation);

    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable long id) {
        int deletedCount = jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id);

        if (deletedCount == 0) {
            throw new NotFoundReservationException();
        }

        return ResponseEntity.noContent().build();
    }

    private final JdbcTemplate jdbcTemplate;

    public ReservationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
