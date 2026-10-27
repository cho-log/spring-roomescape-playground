package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.TimeResponse;
import roomescape.entity.ReservationTime;

import java.time.LocalTime;
import java.util.List;

@RestController"feat: 시간 조회 API 구현"
public class TimeController {

    private final JdbcTemplate jdbcTemplate;

    public TimeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/times")
    public ResponseEntity<List<TimeResponse>> getAllTimes() {
        List<ReservationTime> found = jdbcTemplate.query("select id, time from time", (rs, idx) -> new ReservationTime(rs.getLong("id"), LocalTime.parse(rs.getString("time"))));
        return ResponseEntity.ok(found.stream().map(m -> new TimeResponse(m)).toList());
    }
}
