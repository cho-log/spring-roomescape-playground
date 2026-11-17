package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.data.dto.ReservationTimeRequest;
import roomescape.data.dto.ReservationTimeResponse;
import roomescape.data.service.ReservationTimeService;

@Controller
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/time")
    public String createReservationTimeForm() {
        return "time.html";
    }

    @GetMapping(value = "/times")
    @ResponseBody
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimes() {
        var reservationTimeResponses = reservationTimeService.findAll();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @PostMapping(value = "/times")
    @ResponseBody
    public ResponseEntity<ReservationTimeResponse> createReservationTime(@RequestBody ReservationTimeRequest reservationTimeRequest) {
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.save(reservationTimeRequest);
        return ResponseEntity.created(URI.create("/times/" + reservationTimeResponse.getId())).body(reservationTimeResponse);
    }

    @DeleteMapping(value = "/times/{deletedTimeId}")
    public ResponseEntity<?> deleteReservationTime(@PathVariable Long deletedTimeId) {
        reservationTimeService.deleteById(deletedTimeId);
        return ResponseEntity.noContent().build();
    }
}
