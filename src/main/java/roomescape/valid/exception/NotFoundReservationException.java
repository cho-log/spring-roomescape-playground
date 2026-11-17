package roomescape.valid.exception;

import roomescape.valid.ErrorCode;

public class NotFoundReservationException extends IllegalArgumentException {
    public NotFoundReservationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
