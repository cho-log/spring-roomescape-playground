package roomescape.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.ReservationNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(ReservationRepository.class)
public class ReservationRepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 존재하지_않는_예약을_삭제하면_예외가_발생한다() {
        assertThatThrownBy(() -> reservationRepository.deleteById(Long.MAX_VALUE))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage("존재하지 않는 예약입니다. id: " + Long.MAX_VALUE);
    }

    @Test
    void 중복된_예약을_저장하면_예외가_발생한다() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.now();

        reservationRepository.save("brown", date, time);

        assertThatThrownBy(
                () -> reservationRepository.save("brown2", date, time)
        ).isInstanceOf(DuplicateReservationException.class)
         .hasMessage("이미 예약된 시각: " + LocalDateTime.of(date, time));
    }

    @Test
    void 유효하지_않은_날짜는_저장할_수_없다() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "brown",
                "2099-02-31",
                "10:00"
        )).isInstanceOf(DataAccessException.class);
    }

    @Test
    void 유효하지_않은_시간은_저장할_수_없다() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "brown",
                LocalDate.now().plusDays(1),
                "26:00"
        )).isInstanceOf(DataAccessException.class);
    }
}
