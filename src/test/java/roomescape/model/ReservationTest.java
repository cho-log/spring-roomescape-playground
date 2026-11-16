package roomescape.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import roomescape.exception.InvalidReservationException;

class ReservationTest {
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final LocalTime TEN_O_CLOCK = LocalTime.of(10, 0);

    @Test
    void 이름이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Reservation(null, TOMORROW, TEN_O_CLOCK))
                .isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void 이름이_비어_있으면_예외가_발생한다() {
        assertThatThrownBy(() -> new Reservation("", TOMORROW, TEN_O_CLOCK))
                .isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void 이름이_비어_있으면_공백이면_발생한다() {
        assertThatThrownBy(() -> new Reservation(" ", TOMORROW, TEN_O_CLOCK))
                .isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void 날짜가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Reservation("브라운", null, TEN_O_CLOCK))
                .isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void 시간이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Reservation("브라운", TOMORROW, null))
                .isInstanceOf(InvalidReservationException.class);
    }
}
