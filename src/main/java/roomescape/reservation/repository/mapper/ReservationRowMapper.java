package roomescape.reservation.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import roomescape.reservation.dto.Reservation;
import roomescape.time.dto.Time;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReservationRowMapper implements RowMapper<Reservation> {
    @Override
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Reservation(
                rs.getLong("ID"),
                rs.getString("NAME"),
                rs.getString("DATE"),
                new Time(rs.getLong("TIME_ID"), rs.getString("TIME_VALUE"))
        );
    }
}
