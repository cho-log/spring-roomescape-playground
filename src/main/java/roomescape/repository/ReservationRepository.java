package roomescape.repository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.ReservationNotFoundException;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public ReservationRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    public List<Reservation> findAll() {
        String sql = "SELECT id, name, date, time FROM reservation";

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> Reservation.restore(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getObject("date", LocalDate.class),
                        resultSet.getObject("time", LocalTime.class)
                )
        );
    }

    public Reservation save(Reservation reservation) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(reservation);

        try {
            long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
            return Reservation.restore(
                    id,
                    reservation.getName(),
                    reservation.getDate(),
                    reservation.getTime()
            );
        } catch (DuplicateKeyException e) {
            throw new DuplicateReservationException(
                    reservation.getDate(),
                    reservation.getTime()
            );
        }
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);

        if (count == 0) {
            throw new ReservationNotFoundException(id);
        }
    }
}
