package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import roomescape.domain.Time;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TimeRepository {

    private final JdbcTemplate jdbcTemplate;

    public TimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Time findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT id, time FROM time WHERE id = ?",
                (rs, rowNum) -> new Time(
                        rs.getLong("id"),
                        rs.getString("time")
                ),
                id
        );
    }

    public List<Time> findAll() {
        return jdbcTemplate.query(
                "SELECT id, time FROM time",
                (rs, rowNum) -> new Time(
                        rs.getLong("id"),
                        rs.getString("time")
                )
        );
    }

    public Time save(Time time) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO time (time) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, time.getTime());

            return statement;
        }, keyHolder);

        Number generatedKey = keyHolder.getKey();

        if (generatedKey == null) {
            throw new IllegalStateException("생성된 시간 ID를 가져오지 못했습니다.");
        }

        return new Time(
                generatedKey.longValue(),
                time.getTime()
        );
    }

    public int deleteById(long id) {
        return jdbcTemplate.update(
                "DELETE FROM time WHERE id = ?",
                id
        );
    }
}