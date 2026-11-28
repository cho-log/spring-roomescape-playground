package roomescape.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.InvalidReservationException;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationTest {
    private static final LocalDate DATE = LocalDate.now().plusDays(1);
    private static final LocalTime TIME = LocalTime.now();

    @Test
    void 올바른_예약_정보로_예약을_생성한다() {
        Reservation reservation = Reservation.restore(1, "brown", DATE, TIME);

        assertThat(reservation.getId()).isEqualTo(1L);
        assertThat(reservation.getName()).isEqualTo("brown");
        assertThat(reservation.getDate()).isEqualTo(DATE);
        assertThat(reservation.getTime()).isEqualTo(TIME);
    }

    @Test
    void 예약_ID가_양수가_아니면_예외가_발생한다() {
        assertThatThrownBy(() -> Reservation.restore(0, "brown", DATE, TIME))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("예약 ID는 양수여야 합니다.");
    }

    @Test
    void 이름이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Reservation.restore(1, null, DATE, TIME))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이름은 비어있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void 이름이_비어있으면_예외가_발생한다(String name) {
        assertThatThrownBy(() -> Reservation.restore(1, name, DATE, TIME))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이름은 비어있을 수 없습니다.");
    }

    @Test
    void 이름이_255자면_예약을_생성할_수_있다() {
        String name = "a".repeat(255);

        assertThatCode(() -> Reservation.restore(1, name, DATE, TIME))
                .doesNotThrowAnyException();
    }

    @Test
    void 이름이_255자를_넘으면_예외가_발생한다() {
        String name = "a".repeat(256);

        assertThatThrownBy(() -> Reservation.restore(1, name, DATE, TIME))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이름은 255자 이하여야 합니다.");
    }

    @Test
    void 날짜가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Reservation.restore(1, "brown", null, TIME))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("날짜는 비어있을 수 없습니다.");
    }

    @Test
    void 시간이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Reservation.restore(1, "brown", DATE, null))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("시간은 비어있을 수 없습니다.");
    }
}
