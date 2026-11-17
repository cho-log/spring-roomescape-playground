package roomescape.reservation.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.reservation.dto.Reservation;
import roomescape.reservation.repository.mapper.ReservationRowMapper;
import roomescape.time.dto.Time;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcReservationRepository implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ReservationRowMapper reservationRowMapper;

    public JdbcReservationRepository(JdbcTemplate jdbcTemplate, ReservationRowMapper reservationRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.reservationRowMapper = reservationRowMapper;
    }

    @Override
    public List<Reservation> getAllReservations() {
        String sql = "SELECT r.id AS reservation_id, r.name, r.date, t.id AS time_id, t.time AS time_value FROM reservation AS r INNER JOIN time AS t ON r.time_id = t.id";
        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    @Override
    public Long createReservation(Reservation reservation, Time existingTime) {
        /*  Time 객체의 id를 Reservation 테이블에 Insert */
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", reservation.getName());
        parameters.put("date", reservation.getDate());
        parameters.put("time_id", existingTime.getId());
        return (Long) simpleJdbcInsert.executeAndReturnKey(parameters);
    }

    @Override
    public Integer countReservation(Long id) {
        String selectQuery = "SELECT COUNT(*) FROM reservation WHERE id = ?";
        return jdbcTemplate.queryForObject(selectQuery, Integer.class, id);
    }

    @Override
    public boolean existsReservationById(Long id) {
        String query = "SELECT EXISTS (SELECT 1 FROM reservation WHERE time_id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, id);
    }

    @Override
    public void deleteReservationById(Long id) {
        String deleteQuery = "DELETE FROM reservation WHERE id = ?";
        jdbcTemplate.update(deleteQuery, id);
    }

}
