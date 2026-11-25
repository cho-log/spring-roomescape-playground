package roomescape.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.exception.DuplicateReservationException;

@Repository
public class ReservationDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> rowMapper = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getObject("date", LocalDate.class),
            resultSet.getObject("time", LocalTime.class)
    );

    public ReservationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Reservation reservation) {
        String sql = "INSERT INTO reservation (name, date, time) VALUES (:name, :date, :time)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", reservation.getName())
                .addValue("date", reservation.getDate())
                .addValue("time", reservation.getTime());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        } catch (DuplicateKeyException e) {
            throw new DuplicateReservationException();
        }

        return keyHolder.getKey().longValue();
    }

    public Optional<Reservation> findById(Long id) {
        String sql = "SELECT id, name, date, time FROM reservation WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, params, rowMapper).stream()
                .findFirst();
    }

    public List<Reservation> findAll() {
        String sql = "SELECT id, name, date, time FROM reservation";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM reservation WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbcTemplate.update(sql, params);
    }

}
