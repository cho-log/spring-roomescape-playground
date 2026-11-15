package roomescape.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Time;

@Repository
public class TimeDao {

    private final JdbcTemplate jdbcTemplate;

    public TimeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Time> ROW_MAPPER = (rs, rowNum) ->
            new Time(rs.getLong("id"), rs.getString("time"));

    public Time insert(Time time) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO time (time) VALUES (?)", new String[]{"id"});
            ps.setString(1, time.getTime());
            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        return new Time(id, time.getTime());
    }

    public List<Time> findAll() {
        return jdbcTemplate.query("SELECT id, time FROM time", ROW_MAPPER);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM time WHERE id = ?", id);
    }

    public Time findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT id, time FROM time WHERE id = ?", ROW_MAPPER, id);
    }
}