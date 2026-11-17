package roomescape.model;

public class Reservation {
    private int id;
    private String name;
    private String date; // 날짜를 문자열로 받음 (예: "2023-05-15")
    private String time; // 시간을 문자열로 받음 (예: "14:30")

    public Reservation() {
    }

    public Reservation(int id, String name, String date, String time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}