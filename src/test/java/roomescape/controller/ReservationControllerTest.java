package roomescape.controller;

import org.junit.jupiter.api.Test;
import roomescape.domain.Reservation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationControllerTest {
    private final ReservationController controller = new ReservationController(null);

    @Test
    void missingNameAndLongNameHaveDifferentMessages() {
        assertInvalid(new Reservation(null, "", "2023-08-05", "15:40"),
                "예약자 이름을 입력해주세요.");
        assertInvalid(new Reservation(null, "a".repeat(256), "2023-08-05", "15:40"),
                "예약자 이름은 255자 이하여야 합니다.");
    }

    @Test
    void missingDateAndTimeHaveSpecificMessages() {
        assertInvalid(new Reservation(null, "브라운", "", "15:40"),
                "예약 날짜를 입력해주세요.");
        assertInvalid(new Reservation(null, "브라운", "2023-08-05", ""),
                "예약 시간을 입력해주세요.");
    }

    private void assertInvalid(Reservation reservation, String message) {
        assertThatThrownBy(() -> controller.addReservation(reservation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }
}
