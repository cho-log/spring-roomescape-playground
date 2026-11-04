package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingViewController {
    @GetMapping("/")
    public String createGreetingView() {
        return "/home";
    }
}
