package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.Time;
import roomescape.service.TimeService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Controller
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping("/time")
    public String timePage() {
        return "time";
    }

    @GetMapping("/times")
    @ResponseBody
    public List<Time> getTimes() {
        return timeService.getTimes();
    }

    @PostMapping("/times")
    public ResponseEntity<Time> addTime(
            @RequestBody Map<String, String> request
    ) {
        Time time = timeService.addTime(request.get("time"));

        return ResponseEntity
                .created(URI.create("/times/" + time.getId()))
                .body(time);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable long id) {
        timeService.deleteTime(id);

        return ResponseEntity.noContent().build();
    }
}