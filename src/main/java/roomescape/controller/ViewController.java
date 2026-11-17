package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String createHomeView() {
        return "home";
    }

    @GetMapping("/reservation")
    public String createReservationView() {
        return "reservation";
    }

}
