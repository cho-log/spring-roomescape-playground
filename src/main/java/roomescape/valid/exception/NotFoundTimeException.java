package roomescape.valid.exception;

import roomescape.valid.ErrorCode;

public class NotFoundTimeException extends IllegalArgumentException{
    public NotFoundTimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
