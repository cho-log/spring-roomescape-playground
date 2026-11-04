package roomescape;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ReservationController {
    List<Reservation> reservations = new ArrayList<>();

    @GetMapping("/reservation")
    public String world(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "reservation";
    }

    @ResponseBody
    @GetMapping("/reservations")
    public List<Reservation> json() {
        reservations.add(new Reservation(1, "a", "b", "c"));
        reservations.add(new Reservation(2, "a", "b", "c"));
        return reservations;
    }
}
