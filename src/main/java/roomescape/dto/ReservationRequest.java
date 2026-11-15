package roomescape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationRequest {

    private String name;
    private String date;
    @JsonProperty("time")
    private Long timeId;

    public ReservationRequest() {}

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }

}
