package roomescape.repository;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDetails;

@Repository
public class ReservationRepository {
    private static final RowMapper<Reservation> ROW_MAPPER = (resultSet, rowNumber) ->
            new Reservation(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("date"), resultSet.getString("time"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert reservationInsert;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.reservationInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingColumns("name", "date", "time")
                .usingGeneratedKeyColumns("id");
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query("SELECT id, name, date, time FROM reservation ORDER BY id", ROW_MAPPER);
    }

    public Reservation save(ReservationDetails details) {
        Number key = reservationInsert.executeAndReturnKey(Map.of(
                "name", details.getName(), "date", details.getDate().toString(),
                "time", details.getTime().toString()));
        return new Reservation(key.longValue(), details);
    }
}
