package roomescape.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.model.Reservation;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertReservation;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        insertReservation = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    private RowMapper<Reservation> reservationRowMapper() {
        return (rs, rowNum) -> new Reservation(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("date"),
                rs.getString("time")
        );
    }

    public List<Reservation> selectAll() {
        String sql = "SELECT id, name, date, time FROM reservation";
        return jdbcTemplate.query(sql, reservationRowMapper());
    }

    public Reservation insert(String name, String date, String time) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("date", date);
        parameters.put("time", time);
        Long newId = insertReservation.executeAndReturnKey(parameters).longValue();
        return new Reservation(newId, name, date, time);
    }

    public int delete(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
