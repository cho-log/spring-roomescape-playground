package roomescape.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Time;
import roomescape.valid.ErrorCode;
import roomescape.valid.exception.NotFoundTimeException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TimeDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public TimeDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("time")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(Time time) {
        Map<String, Object> param = new HashMap<>();
        param.put("time", time.getTime());
        return jdbcInsert.executeAndReturnKey(param).longValue();
    }

    public List<Time> findAllTime() {
        return jdbcTemplate.query("select id, time from time",
                (resultSet, rowNum) -> {
                    return new Time(
                        resultSet.getLong("id"),
                        resultSet.getString("time")
                );
        });
    }

    public Time findById(Long id) {
        return jdbcTemplate.queryForObject("select id, time from time where id=?",
                (resultSet, rowNum) -> {
                    return new Time(
                            resultSet.getLong("id"),
                            resultSet.getString("time")
                    );
                }
                ,id);
    }

    public void delete(Long id) {
        int count = jdbcTemplate.update("DELETE FROM time WHERE id = ?", id);

        if(count == 0) throw new NotFoundTimeException(ErrorCode.TIME_NO_FOUND);
    }
}
