package roomescape.repository;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;

import roomescape.domain.Time;
import roomescape.valid.ErrorCode;
import roomescape.valid.exception.NotFoundReservationException;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReservationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;


    public ReservationDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    public List<Reservation> findAllReservation() {

       return jdbcTemplate.query("""
                       SELECT r.id as reservation_id, 
                       r.name, 
                       r.date, 
                       t.id as time_id, 
                       t.time as time_value 
                       FROM reservation as r inner join time as t on r.time_id = t.id""",
                (resultSet, rowNum) -> {
                    Time time = new Time(
                            resultSet.getLong("time_id"),
                            resultSet.getString("time_value")
                    );
                    return new Reservation(
                           resultSet.getLong("id"),
                           resultSet.getString("name"),
                           resultSet.getDate("date").toLocalDate(),
                           time
                    );
                });
    }

    public Long save(Reservation reservation) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", reservation.getName());
        param.put("date", reservation.getDate());
        param.put("time", reservation.getTime().getId());

        Number number = jdbcInsert.executeAndReturnKey(param);
        return number.longValue();
    }

    public void delete(Long id) {
        int count = jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id);

        if(count == 0) throw new NotFoundReservationException(ErrorCode.RESERVATION_NO_FOUND);
    }
}
