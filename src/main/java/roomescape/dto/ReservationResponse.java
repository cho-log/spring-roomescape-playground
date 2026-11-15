package roomescape.dto;

public class ReservationResponse {

    private Long id;
    private String name;
    private String date;
    private TimeResponse time;

    public ReservationResponse(Long id, String name, String date, TimeResponse time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public TimeResponse getTime() {
        return time;
    }
}
