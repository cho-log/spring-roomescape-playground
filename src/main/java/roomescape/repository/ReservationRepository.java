package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.Time;

import java.util.List;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reservation> findAll() {
        String sql = """
            SELECT r.id AS reservation_id,
                   r.name,
                   r.date,
                   t.id AS time_id,
                   t.time AS time_value
            FROM reservation r
            INNER JOIN time t ON r.time_id = t.id
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Time time = new Time(
                    rs.getLong("time_id"),
                    rs.getString("time_value")
            );

            return new Reservation(
                    rs.getLong("reservation_id"),
                    rs.getString("name"),
                    rs.getString("date"),
                    time
            );
        });
    }
}
