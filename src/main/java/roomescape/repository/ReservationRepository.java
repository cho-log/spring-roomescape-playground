package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.ReservationNotFoundException;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reservation> findAll() {
        String sql = "SELECT id, name, date, time FROM reservation";

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> new Reservation(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        LocalDate.parse(resultSet.getString("date")),
                        LocalTime.parse(resultSet.getString("time"))
                )
        );
    }

    public synchronized Reservation save(String name, LocalDate date, LocalTime time) {
        String dateValue = date.toString();
        String timeValue = time.toString();

        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reservation where date = ? AND time = ?",
                Integer.class,
                date.toString(),
                time.toString()
        );

        if (count > 0) {
            throw new DuplicateReservationException(date, time);
        }

        String insertSql = "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    insertSql,
                    new String[]{"id"}
            );

            ps.setString(1, name);
            ps.setString(2, dateValue);
            ps.setString(3, timeValue);

            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();

        return new Reservation(id, name, date, time);
    }

    public synchronized void deleteById(long id) {
        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reservation where id = ?",
                Integer.class,
                id
        );

        if (count == 0) {
            throw new ReservationNotFoundException(id);
        }

        String sql = "DELETE FROM reservation WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
