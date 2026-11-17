package roomescape.time.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.time.dto.Time;
import roomescape.time.service.TimeService;

import java.net.URI;
import java.util.List;

@Controller
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping("/times")
    @ResponseBody
    public List<Time> getTimes() {
        return timeService.getTimes();
    }

    @PostMapping("/times")
    public ResponseEntity<Time> createTime(@RequestBody @Valid Time time) {
        Time newTime = timeService.createTime(time);
        return ResponseEntity.created(URI.create("/times/" + newTime.getId())).body(newTime);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable Long id) {
        timeService.deleteTimeById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
