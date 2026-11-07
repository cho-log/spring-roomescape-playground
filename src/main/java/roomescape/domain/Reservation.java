package roomescape.domain;

public class Reservation {

    public Long id; //atomic 쓰려면 long으로 해야함
    public String name;
    public String date;
    public String time;

    public Reservation(String name, String date, String time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
