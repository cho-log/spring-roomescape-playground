package roomescape.data.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.data.dao.daoInterface.ReservationTimeDao;
import roomescape.data.dto.ReservationTimeRequest;
import roomescape.data.entity.ReservationTime;

@Repository
public class ReservationTimeDaoImpl implements ReservationTimeDao {

    private final JdbcTemplate jdbcTemplate;

    public ReservationTimeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ReservationTime> rowMapper = (rs, rowNum) -> {
        return new ReservationTime(
                rs.getLong("id"),
                rs.getTime("time").toLocalTime()
        );
    };

    @Override
    public ReservationTime save(ReservationTimeRequest reservationTimeRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "insert into time (time) values (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setTime(1, java.sql.Time.valueOf(reservationTimeRequest.getTime()));
            return ps;
        }, keyHolder);
        return ReservationTime.builder()
                .id(keyHolder.getKey().longValue())
                .time(reservationTimeRequest.getTime())
                .build();
    }

    @Override
    public List<ReservationTime> findAll() {
        final String sql = "select id, time from time";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public ReservationTime findById(long id) {
        final String sql = "select id, time from time where id = ?";
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, rowMapper, id));
    }

    @Override
    public void deleteById(long id) {
        final String sql = "delete from time where id = ?";
        int deleted = jdbcTemplate.update(sql, id);
    }
}
