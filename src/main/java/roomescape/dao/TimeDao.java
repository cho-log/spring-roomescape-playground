package roomescape.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.entity.ReservationTime;

import java.sql.PreparedStatement;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TimeDao {

    private final JdbcTemplate jdbcTemplate;

    public TimeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReservationTime> findAll() {
        return jdbcTemplate.query(
                "select id, time from time",
                (rs, idx) -> new ReservationTime(rs.getLong("id"), LocalTime.parse(rs.getString("time"))));
    }

    public Optional<ReservationTime> findById(Long id) {
        List<ReservationTime> times = jdbcTemplate.query(
                "select id, time from time where id = ?",
                (rs, idx) -> new ReservationTime(rs.getLong("id"), LocalTime.parse(rs.getString("time"))),
                id);
        return times.stream().findFirst();
    }

    public ReservationTime save(ReservationTime reservationTime) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement("insert into time (time) values (?)", new String[]{"id"});
            ps.setString(1, reservationTime.getTime().toString());
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKey().longValue();
        return new ReservationTime(newId, reservationTime.getTime());
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("delete from time where id = ?", id);
    }
}
