package roomescape.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.dto.TimeResponse;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
public class ReservationController {

    private final JdbcTemplate jdbcTemplate;

    public ReservationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<Reservation> found = jdbcTemplate.query("""
            select r.id as reservation_id, r.name, r.date, t.id as time_id, t.time as time_value
            from reservation as r
            inner join time as t on r.time_id = t.id"""
                , (rs, idx) ->
                new Reservation(
                        rs.getLong("reservation_id"),
                        rs.getString("name"),
                        LocalDate.parse(rs.getString("date")),
                        new ReservationTime(rs.getLong("time_id"), LocalTime.parse(rs.getString("time_value")))
                )
        );

        return ResponseEntity.ok(found.stream().map(ReservationResponse::new).toList());
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationTime reservationTime = jdbcTemplate.queryForObject(
                "select id, time from time where id = ?",
                (rs, idx) -> new ReservationTime(rs.getLong("id"), LocalTime.parse(rs.getString("time"))),
                request.getTime());

        Reservation validatedReservation = new Reservation(null, request.getName(), request.getDate(), reservationTime);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement("insert into reservation (name, date, time_id) values (?, ?, ?)", new String[]{"id"});
            ps.setString(1, validatedReservation.getName());
            ps.setString(2, validatedReservation.getDate().toString());
            ps.setLong(3, reservationTime.getId());
            return ps;
        }, keyHolder);
        Long newId = keyHolder.getKey().longValue();

        ReservationResponse response = new ReservationResponse(newId, validatedReservation.getName(), validatedReservation.getDate(), new TimeResponse(reservationTime));
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", "/reservations/" + newId).body(response);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable long id) {
        int deleted = jdbcTemplate.update("delete from reservation where id = ?;", id);
        if (deleted == 0) {
            throw new NotFoundException("예약을 찾을 수 없습니다. id =" + id);
        }
        return ResponseEntity.noContent().build();
    }

}
