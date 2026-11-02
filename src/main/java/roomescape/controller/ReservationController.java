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
    private static final int MAX_NAME_LENGTH = 255;

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
        validateReservation(request);

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

    private void validateReservation(Reservation request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("예약자 이름을 입력해주세요.");
        }
        if (request.getName().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "예약자 이름은 " + MAX_NAME_LENGTH + "자 이하여야 합니다."
            );
        }
        if (request.getDate() == null || request.getDate().isBlank()) {
            throw new IllegalArgumentException("예약 날짜를 입력해주세요.");
        }
        if (request.getTime() == null || request.getTime().isBlank()) {
            throw new IllegalArgumentException("예약 시간을 입력해주세요.");
        }
        try {
            LocalDate.parse(request.getDate());
            LocalTime.parse(request.getTime());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 또는 시간 형식이 올바르지 않습니다.");
        }
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
