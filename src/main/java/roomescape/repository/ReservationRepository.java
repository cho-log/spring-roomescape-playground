package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.ReservationNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private long nextReservationId = 1;
    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public synchronized List<Reservation> findAll() {
        String sql = "SELECT id, name, date, time FROM reservation";

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> new Reservation(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        LocalDate.parse(resultSet.getString("date")),
                        LocalTime.parse(resultSet.getString("time"))
                )
        );
    }

    public synchronized Reservation save(String name, LocalDate date, LocalTime time) {
        boolean alreadyReserved = reservations.stream()
                                              .anyMatch(reservation ->
                                                                reservation.getDate().equals(date)
                                                                        && reservation.getTime().equals(time)
                                              );

        if (alreadyReserved) {
            throw new DuplicateReservationException(date, time);
        }

        Reservation createdReservation = new Reservation(
                nextReservationId++,
                name,
                date,
                time
        );

        reservations.add(createdReservation);
        return createdReservation;
    }

    public synchronized void deleteById(long id) {
        boolean removed = reservations.removeIf(
                reservation -> reservation.hasId(id)
        );

        if (!removed) {
            throw new ReservationNotFoundException(id);
        }
    }
}
