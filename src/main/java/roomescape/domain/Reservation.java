package roomescape.domain;

public class Reservation {
    private final Long id;
    private final String name;
    private final String date;
    private final Time time;

    public Reservation(Long id, String name, String date, Time time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public Time getTime() { return time; }
}
