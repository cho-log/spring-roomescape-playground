package roomescape.repository;

import java.util.List;
import java.util.Map;
import org.springframework.dao.DataRetrievalFailureException;
import roomescape.exception.ReservationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDetails;

@Repository
public class ReservationRepository {
    private static final RowMapper<Reservation> ROW_MAPPER = (resultSet, rowNumber) -> {
        try {
            return new Reservation(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("date"), resultSet.getString("time"));
        } catch (ReservationException exception) {
            throw new DataRetrievalFailureException("저장된 예약 데이터가 유효하지 않습니다.", exception);
        }
    };

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

    public boolean deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id) > 0;
    }
}
