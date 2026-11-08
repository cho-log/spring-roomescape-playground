package roomescape.domain;

public class Reservation {

    private final Long id; //atomic 쓰려면 long으로 해야함
    private final String name;
    private final String date;
    private final String time;

    public Reservation(Long id, String name, String date, String time) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.id = id;
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

    public String getTime() {
        return time;
    }
    //public void setId(Long id) {
    //    this.id = id;
    //}
}
