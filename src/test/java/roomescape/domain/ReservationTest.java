package roomescape.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {
    @Test
    void identifiesReservationById() {
        // 준비
        Reservation reservation = new Reservation(1L, "브라운", "2023-08-05", "15:40");
        // 실행 및 검증
        assertThat(reservation.hasId(1L)).isTrue();
        assertThat(reservation.hasId(2L)).isFalse();
    }

    @Test
    void rejectsMissingId() {
        // 실행 및 검증
        assertThatThrownBy(() -> new Reservation(null, "브라운", "2023-08-05", "15:40"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 id는 필수입니다.");
    }
}
