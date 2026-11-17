package roomescape.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationJdbcRepository implements ReservationRepositoryImpl{

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> reservationRowMapper = (rs, rowNum) -> {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setName(rs.getString("name"));
        reservation.setDate(rs.getDate("date").toLocalDate());
        reservation.setTime(rs.getTime("time").toLocalTime());
        return reservation;
    };

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservation";
        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reservation.getName());
            ps.setDate(2, java.sql.Date.valueOf(reservation.getDate()));
            ps.setTime(3, java.sql.Time.valueOf(reservation.getTime()));
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        reservation.setId(generatedId);
        return reservation;
    }

    @Override
    public Reservation findById(Long id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, reservationRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
