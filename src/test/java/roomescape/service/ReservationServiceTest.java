package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.dto.ReservationRequest;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.NotFoundReservationException;
import roomescape.exception.PastReservationException;
import roomescape.repository.ReservationRepository;

@JdbcTest
class ReservationServiceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(new ReservationRepository(jdbcTemplate));
    }

    @Test
    void 존재하지_않는_예약을_삭제하면_예외가_발생한다() {
        assertThatThrownBy(() -> reservationService.delete(1))
                .isInstanceOf(NotFoundReservationException.class);
    }

    @Test
    void 지난_날짜로_예약하면_예외가_발생한다() {
        ReservationRequest request = new ReservationRequest(
                "브라운", LocalDate.now().minusDays(1), LocalTime.of(10, 0));

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(PastReservationException.class);
    }

    @Test
    void 지난_시간으로_예약하면_예외가_발생한다() {
        LocalDateTime oneSecondAgo = LocalDateTime.now()
                .minusSeconds(1)
                .truncatedTo(ChronoUnit.SECONDS);
        ReservationRequest request = new ReservationRequest(
                "브라운", oneSecondAgo.toLocalDate(), oneSecondAgo.toLocalTime());

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(PastReservationException.class);
    }

    @Test
    void 이미_예약된_날짜와_시간으로_예약하면_예외가_발생한다() {
        ReservationRequest request = new ReservationRequest(
                "브라운", LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        reservationService.create(request);

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(DuplicateReservationException.class);
    }
}
