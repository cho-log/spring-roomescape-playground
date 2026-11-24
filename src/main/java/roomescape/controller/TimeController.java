package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.TimeDao;
import roomescape.dto.CreateTimeRequest;
import roomescape.model.Time;

@RestController
public class TimeController {

    private final TimeDao timeDao;

    public TimeController(TimeDao timeDao) {
        this.timeDao = timeDao;
    }

    @PostMapping("/times")
    public ResponseEntity<Time> create(@Valid @RequestBody CreateTimeRequest request) {
        Time saved = timeDao.save(new Time(null, request.time()));
        return ResponseEntity.created(URI.create("/times/" + saved.getId()))
                .body(saved);
    }

    @GetMapping("/times")
    public List<Time> findAll() {
        return timeDao.findAll();
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timeDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
