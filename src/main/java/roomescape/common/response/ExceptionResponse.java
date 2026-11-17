package roomescape.common.response;

public class ExceptionResponse {

    private String message;

    public String getMessage() {
        return message;
    }

    public ExceptionResponse(String message) {
        this.message = message;
    }
}
