package roomescape.time;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.common.BadRequestException;
import roomescape.reservation.Reservation;

@Controller
@RequiredArgsConstructor
class TimeController {
  private final TimeService timeService;

  @GetMapping("/time")
  public String reservation(){
    return "time";
  }

  @GetMapping("/times")
  public ResponseEntity<List<Time>> getTimes() {
    List<Time> times = timeService.getTimes();
    return ResponseEntity.ok()
        .body(times);
  }

  @PostMapping("/times")
  public ResponseEntity<Time> createTime(@RequestBody Time time) throws BadRequestException {
    final boolean isEmpty = time.getTime().isEmpty();
    if (isEmpty) {
      throw new BadRequestException("필수 정보가 누락되었습니다");
    }

    Long id = timeService.addTime(time);
    return ResponseEntity.created(URI.create("/times/" + id))
        .body(time);
  }

  @DeleteMapping("/times/{id}")
  public ResponseEntity<Void> deleteTime(@PathVariable Long id) throws BadRequestException {
    final boolean deleted = timeService.deleteTime(id);
    if (!deleted) {
      throw new BadRequestException("해당하는 시간이 존재하지 않습니다");
    }
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<String> handleException(BadRequestException e) {
    return ResponseEntity.badRequest()
        .body(e.getMessage());
  }
}
