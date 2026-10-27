package roomescape.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.TimeRequest;
import roomescape.dto.TimeResponse;
import roomescape.entity.ReservationTime;
import roomescape.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.time.LocalTime;
import java.util.List;

@RestController
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

    @PostMapping("/times")
    public ResponseEntity<TimeResponse> createTime(@RequestBody TimeRequest timeRequest) {
        ReservationTime validateTime = new ReservationTime(null,timeRequest.getTime());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(c ->{
            PreparedStatement st = c.prepareStatement("insert into time (time) values (?)",new String[]{"id"});
            st.setString(1, validateTime.getTime().toString());
            return st;
        },keyHolder);

        Long newId = keyHolder.getKey().longValue();

        TimeResponse newTime = new TimeResponse(newId, validateTime.getTime());
        return ResponseEntity.status(HttpStatus.CREATED).header("Location","/times/"+newId).body(newTime);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable long id) {
        int deleted = jdbcTemplate.update("delete from time where id = ?", id);
        if (deleted == 0) {
            throw new NotFoundException("시간을 찾을 수 없습니다. id = " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
