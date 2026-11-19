package roomescape.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.model.Reservation;

@Repository
public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong index = new AtomicLong(1);
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
        Reservation savedReservation = new Reservation(
                index.getAndIncrement(),
                reservation.getName(),
                reservation.getDate(),
                reservation.getTime()
        );
        reservations.add(savedReservation);
        return savedReservation;
    }

    public boolean existsByDateAndTime(LocalDate date, LocalTime time) {
        return reservations.stream()
                .anyMatch(r -> r.getDate().equals(date)
                        && r.getTime().equals(time));
    }

    public List<Reservation> findAll() {
        List<Reservation> allReservations = jdbcTemplate.query(
                "SELECT id, name, date, time FROM reservation", reservationRowMapper
        );

        return allReservations;
    }

    public Optional<Reservation> findById(long id) {
        return reservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
    }

    public void delete(Reservation reservation) {
        reservations.remove(reservation);
    }
}
