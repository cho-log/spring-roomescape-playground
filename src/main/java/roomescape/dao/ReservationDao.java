package roomescape.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class ReservationDao {

    private final JdbcTemplate jdbcTemplate;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query("""
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
    }

    public Reservation save(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(c -> {
            PreparedStatement ps = c.prepareStatement("insert into reservation (name, date, time_id) values (?, ?, ?)", new String[]{"id"});
            ps.setString(1, reservation.getName());
            ps.setString(2, reservation.getDate().toString());
            ps.setLong(3, reservation.getTime().getId());
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKey().longValue();
        return new Reservation(newId, reservation.getName(), reservation.getDate(), reservation.getTime());
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("delete from reservation where id = ?", id);
    }
}
