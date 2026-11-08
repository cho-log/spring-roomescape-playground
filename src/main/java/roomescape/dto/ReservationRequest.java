package roomescape.dto;

public record ReservationRequest(String name, String date, String time) { }
//record는 데이터를 담기만 하는 클래스 를 한 줄로 만드는 Java 문법