package roomescape.valid.exception;

import roomescape.valid.ErrorCode;

public class IllegalTimeException extends IllegalArgumentException {
    public IllegalTimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
