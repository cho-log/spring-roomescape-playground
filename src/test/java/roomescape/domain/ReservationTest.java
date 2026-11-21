package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.InvalidReservationException;

public class ReservationTest {
    private static final LocalDate DATE = LocalDate.of(2023, 8, 5);
    private static final LocalTime TIME = LocalTime.of(15, 40);

    @Test
    void 이름_날짜_시간이_있으면_예약을_만든다() {
        assertThatCode(() -> new Reservation(1L, "브라운", DATE, TIME))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void 이름이_비어_있으면_예외가_발생한다(String name) {
        assertThatThrownBy(() -> new Reservation(1L, name, DATE, TIME))
                .isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void 이름이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Reservation(1L, null, DATE, TIME))
                .isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void 날짜가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", null, TIME))
                .isInstanceOf(InvalidReservationException.class);
    }

    @Test
    void 시간이_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", DATE, null))
                .isInstanceOf(InvalidReservationException.class);
    }
}
