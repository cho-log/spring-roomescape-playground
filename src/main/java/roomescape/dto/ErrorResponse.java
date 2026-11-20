package roomescape.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

public record ErrorResponse(
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<String, String> errors
) {
}
