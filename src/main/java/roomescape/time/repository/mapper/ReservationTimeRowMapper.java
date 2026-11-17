package roomescape.time.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import roomescape.time.dto.Time;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReservationTimeRowMapper implements RowMapper<Time> {
    @Override
    public Time mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Time(
                rs.getLong("ID"),
                rs.getString("TIME")
        );
    }
}
