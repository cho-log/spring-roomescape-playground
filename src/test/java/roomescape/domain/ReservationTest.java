package roomescape.domain;

import roomescape.exception.ReservationException;
import roomescape.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {
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
                .isInstanceOf(ReservationException.class)
                .hasMessage("예약자 이름은 필수입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankDate(String date) {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", date, "15:40"))
                .isInstanceOf(ReservationException.class)
                .hasMessage("예약 날짜는 필수입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankTime(String time) {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", "2023-08-05", time))
                .isInstanceOf(ReservationException.class)
                .hasMessage("예약 시간은 필수입니다.");
    }

    @Test
    void storesDateAndTimeAsTemporalTypes() {
        Reservation reservation = new Reservation(1L, "브라운", "2024-02-29", "15:40");
        assertThat(reservation.getDate()).isEqualTo(LocalDate.of(2024, 2, 29));
        assertThat(reservation.getTime()).isEqualTo(LocalTime.of(15, 40));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-02-30", "2026-13-01", "not-a-date"})
    void rejectsInvalidDate(String date) {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", date, "15:40"))
                .isInstanceOfSatisfying(ReservationException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_DATE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"25:00", "15:60", "not-a-time"})
    void rejectsInvalidTime(String time) {
        assertThatThrownBy(() -> new Reservation(1L, "브라운", "2023-08-05", time))
                .isInstanceOfSatisfying(ReservationException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TIME));
    }
}
