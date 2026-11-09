package roomescape.domain;

public class Reservation {
    private long id;
    private String name;
    private String date;
    private String time;

    public Reservation() {}

    public Reservation(long id, String name, String date, String time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }

    public static Reservation toEntity(Reservation reservation, long id) {
        return new Reservation(id, reservation.getName(), reservation.getDate(), reservation.getTime());
    }

    public boolean hasId(long id) {
        return this.id == id;
    }
}
