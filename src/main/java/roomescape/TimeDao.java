package roomescape;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.model.Time;

@Repository
public class TimeDao {

    private static final RowMapper<Time> ROW_MAPPER = (rs, rowNum) ->
            new Time(rs.getLong("id"), rs.getString("time"));

    private final JdbcTemplate jdbcTemplate;

    public TimeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Time> findAll() {
        return jdbcTemplate.query(
                "SELECT id, time FROM time ORDER BY id", ROW_MAPPER);
    }

    public Time save(Time time) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO time (time) VALUES (?)", new String[]{"id"});
            statement.setString(1, time.getTime());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("생성된 시간 ID를 가져올 수 없습니다.");
        }

        return new Time(key.longValue(), time.getTime());
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM time WHERE id = ?", id);
    }
}
