package roomescape.domain;

public class Reservation {
    private final Long id;
    private final String name;
    private final String date;
    private final String time;

    public Reservation(Long id, String name, String date, String time) {
        if (id == null) {
            throw new IllegalArgumentException("예약 id는 필수입니다.");
        }
        validateRequired(name, "예약자 이름은 필수입니다.");
        validateRequired(date, "예약 날짜는 필수입니다.");
        validateRequired(time, "예약 시간은 필수입니다.");
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public boolean hasId(Long id) {
        return this.id.equals(id);
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
}
