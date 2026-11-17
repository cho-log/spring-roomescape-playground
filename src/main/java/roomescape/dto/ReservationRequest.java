package roomescape.dto;

import roomescape.domain.Reservation;
import roomescape.exception.InvalidReservationException;

public record ReservationRequest(String name, String date, String time) {

    public Reservation toEntity() {
        validate(name, date, time);
        return new Reservation(name, date, time);
    }

    private void validate(String... values) {
        for (String value : values) {
            validateNotBlank(value);
        }
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidReservationException("예약 정보를 입력해야 합니다.");
        }
    }
}
