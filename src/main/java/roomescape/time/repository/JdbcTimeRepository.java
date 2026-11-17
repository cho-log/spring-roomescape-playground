package roomescape.time.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import roomescape.time.dto.Time;
import roomescape.time.repository.mapper.ReservationTimeRowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTimeRepository implements TimeRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ReservationTimeRowMapper reservationTimeRowMapper;

    public JdbcTimeRepository(JdbcTemplate jdbcTemplate, ReservationTimeRowMapper reservationTimeRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.reservationTimeRowMapper = reservationTimeRowMapper;
    }

    @Override
    public SqlRowSet getTimeRowSet(Time reservationTime) {
        /*  Time 객체의 id를 사용하여 실제 Time 객체를 조회 */
        String selectQuery = "SELECT id, time FROM time WHERE id = ?";
        return jdbcTemplate.queryForRowSet(selectQuery, reservationTime.getId());
    }

    @Override
    public List<Time> getAllTimes() {
        String sql = "SELECT id, time FROM time";
        return jdbcTemplate.query(sql, reservationTimeRowMapper);
    }

    @Override
    public Long createTime(Time time) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("time")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("time", time.getTime());
        return (Long) simpleJdbcInsert.executeAndReturnKey(parameters);
    }

    @Override
    public Integer countTimeById(Long id) {
        String selectQuery = "SELECT COUNT(*) FROM time WHERE id = ?";
        return jdbcTemplate.queryForObject(selectQuery, Integer.class, id);
    }

    @Override
    public void deleteTimeById(Long id) {
        String deleteQuery = "DELETE FROM time WHERE id = ?";
        jdbcTemplate.update(deleteQuery, id);
    }
}
