package roomescape.repository;

import java.util.List;
import java.util.Map;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDetails;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;

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
        try {
            Number key = reservationInsert.executeAndReturnKey(Map.of(
                    "name", details.getName(), "date", details.getDate().toString(),
                    "time", details.getTime().toString()));
            return new Reservation(key.longValue(), details);
        } catch (DuplicateKeyException exception) {
            // 자동 생성 id의 충돌까지 예약 시간 중복으로 바꾸지 않는다.
            if (existsAt(details)) {
                throw new ReservationException(ErrorCode.DUPLICATE_RESERVATION);
            }
            throw exception;
        }
    }

    private boolean existsAt(ReservationDetails details) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM reservation WHERE date = ? AND time = ?)",
                Boolean.class, details.getDate().toString(), details.getTime().toString()));
    }

    public boolean deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id) > 0;
    }
}
