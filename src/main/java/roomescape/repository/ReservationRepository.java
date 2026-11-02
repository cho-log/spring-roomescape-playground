package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.Time;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;

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
    public Reservation save(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                    INSERT INTO reservation (name, date, time_id)
                    VALUES (?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, reservation.getName());
            statement.setString(2, reservation.getDate());
            statement.setLong(3, reservation.getTime().getId());

            return statement;
        }, keyHolder);

        Number generatedKey = keyHolder.getKey();

        if (generatedKey == null) {
            throw new IllegalStateException(
                    "생성된 예약 ID를 가져오지 못했습니다."
            );
        }

        return new Reservation(
                generatedKey.longValue(),
                reservation.getName(),
                reservation.getDate(),
                reservation.getTime()
        );
    }

    public int deleteById(long id) {
        return jdbcTemplate.update(
                "DELETE FROM reservation WHERE id = ?",
                id
        );
    }
}
