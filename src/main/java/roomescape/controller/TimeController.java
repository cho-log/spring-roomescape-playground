package roomescape.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.Time;
import roomescape.repository.TimeDao;
import roomescape.valid.exception.IllegalTimeException;
import roomescape.valid.exception.NotFoundTimeException;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
public class TimeController {
    private final TimeDao timeDao;

    @Autowired
    public TimeController(TimeDao timeDao) {
        this.timeDao = timeDao;
    }

    @PostMapping("/times")
    public ResponseEntity<Time> setTime(@RequestBody Time time) {
        log.info("time = {}", time.getTime());
        Long id = timeDao.save(time);
        Time newTime = time.toEntity(id);
        return ResponseEntity.created(URI.create("/times/" + id)).body(newTime);
    }

    @GetMapping("/times")
    public ResponseEntity<List<Time>> getTime() {
        return ResponseEntity.ok(timeDao.findAllTime());
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable Long id) {
        timeDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({NotFoundTimeException.class, IllegalTimeException.class})
    public ResponseEntity<Void> handleException(Exception e) {
        log.info("error message = {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
