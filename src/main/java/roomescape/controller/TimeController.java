package roomescape.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.TimeRequest;
import roomescape.dto.TimeResponse;
import roomescape.entity.ReservationTime;
import roomescape.service.TimeService;

import java.util.List;

@RestController
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping("/times")
    public ResponseEntity<List<TimeResponse>> getAllTimes() {
        List<ReservationTime> found = timeService.findAll();
        return ResponseEntity.ok(found.stream().map(m -> new TimeResponse(m)).toList());
    }

    @PostMapping("/times")
    public ResponseEntity<TimeResponse> createTime(@RequestBody TimeRequest timeRequest) {
        ReservationTime created = timeService.create(timeRequest.getTime());

        TimeResponse newTime = new TimeResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", "/times/" + created.getId()).body(newTime);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable long id) {
        timeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
