package roomescape.DAO;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.value.*;
import roomescape.exception.ErrorCode;
import roomescape.exception.InvalidReservationException;
import roomescape.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class ReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReservationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Reservation> actorRowMapper = (resultSet, rowNum) -> {
        Reservation reservation = new Reservation(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("date"),
                resultSet.getString("time")
        );
        return reservation;
    };

    public List<Reservation> findAllReservations() {
        String sql = """
        SELECT 
            r.id as reservation_id, 
            r.name, 
            r.date, 
            t.id as time_id, 
            t.time as time_value 
        FROM reservation as r
        INNER JOIN time as t ON r.time_id = t.id
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Reservation(
                rs.getLong("reservation_id"),
                rs.getString("name"),
                rs.getString("date"),
                String.valueOf(new Time(rs.getLong("time_id"), rs.getString("time_value")))
        ));
    }

    public Reservation insertReservation(Reservation reservation) {
        if (StringUtils.isBlank(reservation.getName()) || reservation.getDate() == null || reservation.getTime() == null || reservation.getTimeId() == null) {
            throw new InvalidReservationException(ErrorCode.INVALID_RESERVATION);
        }
        final String sql = "INSERT INTO reservation (name, date, time_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reservation.getName());
            ps.setString(2, String.valueOf(reservation.getDate()));
            ps.setLong(3, reservation.getTimeId());
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKey().longValue();
        return new Reservation(newId, reservation.getName(), String.valueOf(reservation.getDate()), String.valueOf(reservation.getTime()));
    }

    public void deleteReservation(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);

        if (count == 0)  throw new NotFoundException(ErrorCode.RESERVATION_NOT_FOUND);
    }
}