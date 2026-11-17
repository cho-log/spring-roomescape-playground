package roomescape.time;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeService {
  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<Time> rowMapper = (rs, rowNum) ->
      new Time(
          rs.getLong("id"),
          rs.getString("time")
      );

  public List<Time> getTimes() {
    String sql = "SELECT * FROM time";
    return jdbcTemplate.query(sql, rowMapper);
  }

  public String getTime(Long id) {
    String sql = "SELECT time FROM time WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
  }

  public Long addTime(Time time) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    String sql = "INSERT INTO time(time) VALUES (?)";
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, time.getTime());
          return ps;
        }, keyHolder);
    time.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    return time.getId();
  }

  public boolean deleteTime(Long id) {
    String sql = "DELETE FROM time WHERE id = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }
}
