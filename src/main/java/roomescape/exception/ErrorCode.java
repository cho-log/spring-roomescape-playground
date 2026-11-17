package roomescape.exception;

public enum ErrorCode {
    MISSING_NAME("예약자 이름은 필수입니다."),
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
