package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import org.springframework.dao.DataRetrievalFailureException;
import roomescape.exception.ReservationException;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
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
    void reportsWhetherAReservationWasDeletedAndPreservesOthers() {
        Reservation first = reservationRepository.save(new ReservationDetails("첫번째", "2023-08-05", "15:40"));
        Reservation second = reservationRepository.save(new ReservationDetails("두번째", "2023-08-05", "18:00"));

        assertThat(reservationRepository.deleteById(first.getId())).isTrue();
        assertThat(reservationRepository.deleteById(first.getId())).isFalse();
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
    void propagatesDatabaseFailuresInsteadOfReturningEmptyResults() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        SQLException failure = new SQLException("connection unavailable");
        when(dataSource.getConnection()).thenThrow(failure);
        ReservationRepository failingRepository = new ReservationRepository(new JdbcTemplate(dataSource));

        assertThatThrownBy(failingRepository::findAll)
                .isInstanceOf(CannotGetJdbcConnectionException.class).hasRootCause(failure);
    }

    @Test
    void treatsInvalidStoredDataAsADataRetrievalFailure() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운", "2023/08/05", "15:40");
        assertThatThrownBy(reservationRepository::findAll)
                .isInstanceOf(DataRetrievalFailureException.class)
                .hasCauseInstanceOf(ReservationException.class);
    }

    @Test
    void restoresHistoricalReservationsWithoutApplyingNewBookingRules() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운", "2000-01-01", "15:40");
        assertThat(reservationRepository.findAll()).hasSize(1);
    }

}
