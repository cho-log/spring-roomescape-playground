package roomescape.valid.exception;

import roomescape.valid.ErrorCode;

public class IllegalReservationException extends IllegalArgumentException{
    public IllegalReservationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
