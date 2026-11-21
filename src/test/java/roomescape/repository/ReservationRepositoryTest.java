package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDetails;

@JdbcTest
@Import(ReservationRepository.class)
class ReservationRepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void returnsEmptyListWhenNoReservationsExist() {
        assertThat(reservationRepository.findAll()).isEmpty();
    }

    @Test
    void mapsEveryRowInIdOrder() {
        jdbcTemplate.update("INSERT INTO reservation (id, name, date, time) VALUES (?, ?, ?, ?)",
                30L, "두번째", "2024-02-29", "18:00");
        jdbcTemplate.update("INSERT INTO reservation (id, name, date, time) VALUES (?, ?, ?, ?)",
                10L, "첫번째", "2023-08-05", "15:40");

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).extracting(Reservation::getId).containsExactly(10L, 30L);
        assertThat(reservations).extracting(Reservation::getName).containsExactly("첫번째", "두번째");
        assertThat(reservations.get(0).getDate()).isEqualTo(LocalDate.of(2023, 8, 5));
        assertThat(reservations.get(1).getTime()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void returnsTheGeneratedKeyForEachSavedReservation() {
        Reservation first = reservationRepository.save(new ReservationDetails("첫번째", "2023-08-05", "15:40"));
        Reservation second = reservationRepository.save(new ReservationDetails("두번째", "2024-02-29", "18:00"));

        assertThat(first.getId()).isPositive();
        assertThat(second.getId()).isPositive().isNotEqualTo(first.getId());
        assertThat(jdbcTemplate.queryForObject("SELECT name FROM reservation WHERE id = ?",
                String.class, first.getId())).isEqualTo("첫번째");
        assertThat(jdbcTemplate.queryForObject("SELECT name FROM reservation WHERE id = ?",
                String.class, second.getId())).isEqualTo("두번째");
    }

    @Test
    void reportsDeletedRowsAndPreservesOtherReservations() {
        Reservation first = reservationRepository.save(new ReservationDetails("첫번째", "2023-08-05", "15:40"));
        Reservation second = reservationRepository.save(new ReservationDetails("두번째", "2023-08-05", "18:00"));

        assertThat(reservationRepository.deleteById(first.getId())).isEqualTo(1);
        assertThat(reservationRepository.deleteById(first.getId())).isZero();
        assertThat(reservationRepository.findAll()).extracting(Reservation::getId)
                .containsExactly(second.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"오'브라운", "Robert'); DROP TABLE reservation; --"})
    void bindsNamesAsValues(String name) {
        Reservation reservation = reservationRepository.save(new ReservationDetails(name, "2023-08-05", "15:40"));

        assertThat(jdbcTemplate.queryForObject("SELECT name FROM reservation WHERE id = ?",
                String.class, reservation.getId())).isEqualTo(name);
        assertThat(reservationRepository.findAll()).hasSize(1);
    }

    @Test
    void databaseRejectsMissingRequiredValues() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                null, "2023-08-05", "15:40"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(reservationRepository.findAll()).isEmpty();
    }

    @Test
    void propagatesDatabaseFailuresInsteadOfReturningEmptyResults() {
        JdbcTemplate failingTemplate = spy(jdbcTemplate);
        CannotGetJdbcConnectionException failure = new CannotGetJdbcConnectionException("connection unavailable");
        doThrow(failure).when(failingTemplate).query(anyString(), ArgumentMatchers.<RowMapper<Reservation>>any());
        ReservationRepository failingRepository = new ReservationRepository(failingTemplate);

        assertThatThrownBy(failingRepository::findAll).isSameAs(failure);
    }
}
