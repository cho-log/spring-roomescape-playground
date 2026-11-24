package roomescape.domain;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;
import static org.assertj.core.api.Assertions.*;

class ReservationDetailsTest {
    @Test
    void acceptsNameAtTheMaximumLength() {
        assertThat(new ReservationDetails("가".repeat(255), "2023-08-05", "15:40").getName()).hasSize(255);
    }

    @Test
    void rejectsNameOverTheMaximumLength() {
        assertThatThrownBy(() -> new ReservationDetails("가".repeat(256), "2023-08-05", "15:40"))
                .isInstanceOfSatisfying(ReservationException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NAME_TOO_LONG));
    }

    @ParameterizedTest
    @ValueSource(strings = {"15:39:59", "15:40"})
    void rejectsNewBookingsAtOrBeforeNow(String time) {
        ReservationDetails details = new ReservationDetails("브라운", "2023-08-05", time);
        assertThatThrownBy(() -> details.validateBookableAt(LocalDateTime.of(2023, 8, 5, 15, 40)))
                .isInstanceOfSatisfying(ReservationException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAST_RESERVATION));
    }

    @Test
    void acceptsNewBookingsAfterNow() {
        ReservationDetails details = new ReservationDetails("브라운", "2023-08-05", "15:40:00.000000001");
        assertThatCode(() -> details.validateBookableAt(LocalDateTime.of(2023, 8, 5, 15, 40)))
                .doesNotThrowAnyException();
    }
}
