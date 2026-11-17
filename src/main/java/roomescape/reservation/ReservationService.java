package roomescape.reservation;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import roomescape.time.Time;
import roomescape.time.TimeService;

@Service
@RequiredArgsConstructor
public class ReservationService {
  private final TimeService timeService;
  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<Reservation> rowMapper = (rs, rowNum) ->
      new Reservation(
          rs.getLong("id"),
          rs.getString("name"),
          rs.getString("date"),
          new Time(rs.getLong("time_id"), rs.getString("time"))
      );

  public List<Reservation> getReservations() {
    String sql = "SELECT * FROM reservation INNER JOIN time on reservation.time_id = time.id";
    return jdbcTemplate.query(sql, rowMapper);
  }

  public Long addReservation(Reservation reservation) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    String sql = "INSERT INTO reservation(name, date, time_id) VALUES (?, ?, ?)";
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, reservation.getName());
          ps.setString(2, reservation.getDate());
          ps.setLong(3, reservation.getTime().getId());
          return ps;
        }, keyHolder);
    reservation.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    reservation.getTime().setTime(timeService.getTime(reservation.getTime().getId()));
    return reservation.getId();
  }

  public boolean deleteReservation(Long id) {
    String sql = "DELETE FROM reservation WHERE id = ?";
    int rowsAffected = jdbcTemplate.update(sql, id);
    return rowsAffected > 0;
  }
}
