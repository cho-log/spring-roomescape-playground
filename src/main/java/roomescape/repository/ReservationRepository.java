package roomescape.repository;

import static java.util.Collections.unmodifiableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.exception.NotFoundReservationException;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reservation> findAll() {
        //return unmodifiableList(reservations);
        String sql = "SELECT id, name, date, time FROM reservation";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Reservation(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                LocalDate.parse(resultSet.getString("date")),
                LocalTime.parse(resultSet.getString("time"))
        ));
    }

    public Reservation save(Reservation reservation) {
        //Reservation reservation = new Reservation(idGenerator.incrementAndGet(), name, date, time);
        //reservations.add(reservation);
        //return reservation;
        String sql = "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection-> {
            var ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, reservation.getName());
            ps.setString(2, reservation.getDate().toString());
            ps.setString(3, reservation.getTime().toString());
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        return reservation.withId(generatedId);

    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        int delete = jdbcTemplate.update(sql, id);
        if(delete == 0) {
            throw new NotFoundReservationException("예약 ID " + id + "인 예약을 찾을 수 없습니다.");
        }

        //boolean removed = reservations.removeIf(reservation -> reservation.getId().equals(id));
        //if (!removed) {
        //    throw new NotFoundReservationException("예약 ID " + id + "인 예약을 찾을 수 없습니다.");
        //}
    }


}
