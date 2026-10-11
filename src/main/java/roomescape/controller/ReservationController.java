package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import roomescape.model.Reservation;

@Controller
public class ReservationController {

    @GetMapping("/reservation")
    public String reservation(){
        return "reservation";
    }

    private List<Reservation> reservations=new ArrayList<>();

    @GetMapping("/reservations")
    @ResponseBody // List<Reservation>을 JSON 응답으로 반환
    public List<Reservation> getReservations(Model model){
        reservations.add (new Reservation(1L,"브라운","2023-01-01","10:00"));
        reservations.add (new Reservation(2L,"브라운","2023-01-02","10:00"));
        reservations.add (new Reservation(3L,"브라운","2023-01-03","10:00"));

        model.addAttribute(reservations);
        return reservations;

    }


}
