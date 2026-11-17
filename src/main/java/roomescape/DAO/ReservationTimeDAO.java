package roomescape.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.value.Time;
import roomescape.exception.ErrorCode;
import roomescape.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ReservationTimeDAO {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public ReservationTimeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Time> rowMapper = (rs, rowNum) -> new Time(rs.getLong("id"), rs.getString("time"));

    public Time save(String time) {
        final String sql = "INSERT INTO time (time) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, time);
            return ps;
        }, keyHolder);
        Long newId = keyHolder.getKey().longValue();
        return new Time(newId, time);
    }

    public List<Time> findAll() {
        return jdbcTemplate.query("SELECT * FROM time", rowMapper);
    }

    public void deleteById(Long id) {
        int count = jdbcTemplate.update("DELETE FROM time WHERE id = ?", id);
        if (count == 0) throw new NotFoundException(ErrorCode.TIME_NOT_FOUND);
    }
}
