package roomescape.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;

@Controller
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();

    private void initializeReservations() {
        reservations.add(new Reservation(1, "정명준", "2023-01-01", "10:00"));
        reservations.add(new Reservation(2, "이규형", "2023-01-02", "11:00"));
        reservations.add(new Reservation(3, "이고은", "2023-01-03", "12:00"));
        reservations.add(new Reservation(4, "손서진", "2023-01-04", "13:00"));
        reservations.add(new Reservation(5, "김수아", "2023-01-05", "14:00"));
        reservations.add(new Reservation(6, "김예주", "2023-01-06", "15:00"));
        reservations.add(new Reservation(7, "김민욱", "2023-01-07", "16:00"));
        reservations.add(new Reservation(8, "김하늘", "2023-01-08", "17:00"));
        reservations.add(new Reservation(9, "이우석", "2023-01-09", "18:00"));
        reservations.add(new Reservation(10, "양나은", "2023-01-10", "19:00"));
        reservations.add(new Reservation(11, "문선영", "2023-01-11", "20:00"));
    }

    public ReservationController() {
        initializeReservations();
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation"; //reservation.html을 찾아서 렌더링해라. thymeleaf가 알아서 해줌.
    }


    @GetMapping("reservations")
    @ResponseBody //얘는 그냥 반환값을 그대로 응답으로 보내라는거임. 이거 없으면 reservation.html을 찾으려고함.
    public List<Reservation> getReservations() {
        return reservations;
    }



}
