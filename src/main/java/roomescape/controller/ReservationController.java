package roomescape.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.Reservation;
import roomescape.exception.NotFoundReservationException;
import roomescape.repository.ReservationRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import roomescape.domain.Time;

import java.util.Map;

import java.net.URI;
import java.time.LocalDate;
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
        return "new-reservation";
    }

    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> addReservation(
            @RequestBody Map<String, String> request
    ) {
        String name = request.get("name");
        String date = request.get("date");
        String timeValue = request.get("time");

        if (name == null || name.isBlank() || name.length() > 255
                || date == null || date.isBlank()
                || timeValue == null || timeValue.isBlank()) {
            throw new IllegalArgumentException("예약 정보를 확인해주세요.");
        }

        Long timeId;

        try {
            LocalDate.parse(date);
            timeId = Long.valueOf(timeValue);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IllegalArgumentException(
                    "날짜 또는 시간 ID 형식이 올바르지 않습니다."
            );
        }

        Time time;

        try {
            time = jdbcTemplate.queryForObject(
                    "SELECT id, time FROM time WHERE id = ?",
                    (rs, rowNum) -> new Time(
                            rs.getLong("id"),
                            rs.getString("time")
                    ),
                    timeId
            );
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("등록되지 않은 시간입니다.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                    INSERT INTO reservation (name, date, time_id)
                    VALUES (?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, name);
            statement.setString(2, date);
            statement.setLong(3, timeId);

            return statement;
        }, keyHolder);

        Number generatedKey = keyHolder.getKey();

        if (generatedKey == null) {
            throw new IllegalStateException(
                    "생성된 예약 ID를 가져오지 못했습니다."
            );
        }

        Long newId = generatedKey.longValue();

        Reservation reservation = new Reservation(
                newId,
                name,
                date,
                time
        );

        return ResponseEntity
                .created(URI.create("/reservations/" + newId))
                .body(reservation);
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
    private final ReservationRepository reservationRepository;

    public ReservationController(
            JdbcTemplate jdbcTemplate,
            ReservationRepository reservationRepository)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.reservationRepository = reservationRepository;
    }

}
