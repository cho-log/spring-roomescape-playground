package roomescape.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {
    @Test
    void identifiesReservationById() {
        Reservation reservation = new Reservation(1L, "브라운", "2023-08-05", "15:40");
        assertThat(reservation.hasId(1L)).isTrue();
        assertThat(reservation.hasId(2L)).isFalse();
    }

    @Test
    void rejectsMissingId() {
        assertThatThrownBy(() -> new Reservation(null, "브라운", "2023-08-05", "15:40"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 id는 필수입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankName(String name) {
        assertThatThrownBy(() -> new Reservation(1L, name, "2023-08-05", "15:40"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약자 이름은 필수입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankDate(String date) {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", date, "15:40"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 날짜는 필수입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankTime(String time) {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", "2023-08-05", time))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 시간은 필수입니다.");
    }
}
