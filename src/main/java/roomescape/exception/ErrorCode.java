package roomescape.exception;

public enum ErrorCode {
    INTERNAL_ERROR("요청을 처리하지 못했습니다. 잠시 후 다시 시도해 주세요."),
    INVALID_REQUEST("요청 형식을 확인해 주세요."),
    MISSING_NAME("예약자 이름은 필수입니다."),
    NAME_TOO_LONG("예약자 이름은 255자 이하여야 합니다."),
    MISSING_DATE("예약 날짜는 필수입니다."),
    MISSING_TIME("예약 시간은 필수입니다."),
    INVALID_DATE("유효한 예약 날짜를 입력해 주세요."),
    INVALID_TIME("유효한 예약 시간을 입력해 주세요."),
    RESERVATION_NOT_FOUND("예약을 찾을 수 없습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
