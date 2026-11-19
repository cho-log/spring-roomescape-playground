package roomescape.repository;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.model.Reservation;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> reservationRowMapper = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            LocalDate.parse(resultSet.getString("date")),
            LocalTime.parse(resultSet.getString("time"))
    );

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Reservation save(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO reservation(name, date, time) VALUES (?, ?, ?)",
                    new String[]{"id"}
            );
            preparedStatement.setString(1, reservation.getName());
            preparedStatement.setString(2, reservation.getDate().toString());
            preparedStatement.setString(3, reservation.getTime().toString());
            return preparedStatement;
            }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return new Reservation(id, reservation.getName(), reservation.getDate(), reservation.getTime());
    }


    public boolean existsByDateAndTime(LocalDate date, LocalTime time) {
        return !jdbcTemplate.query(
                "SELECT id, name, date, time FROM reservation WHERE date = ? and time = ?", reservationRowMapper, date.toString(),
                time.toString()
        ).isEmpty();
    }

    public List<Reservation> findAll() {
        List<Reservation> allReservations = jdbcTemplate.query(
                "SELECT id, name, date, time FROM reservation", reservationRowMapper
        );

        return allReservations;
    }

    public Optional<Reservation> findById(long findingId) {
        return jdbcTemplate.query(
                "SELECT id, name, date, time FROM reservation WHERE id = ?", reservationRowMapper, findingId
                )
                .stream()
                .findFirst();
    }

    public void delete(Reservation reservation) {
        jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", reservation.getId());
    }
}
