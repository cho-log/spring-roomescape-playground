package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationPageController {

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation"; //reservation.html을 찾아서 렌더링해라. thymeleaf가 알아서 해줌.
    }
}
