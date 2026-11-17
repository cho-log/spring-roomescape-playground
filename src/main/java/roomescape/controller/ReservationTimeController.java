package roomescape.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.value.Time;
import roomescape.service.ReservationTimeService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    @Autowired
    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping
    public ResponseEntity<Time> addTimeSlot(@RequestBody Time reservationTime) {
        Time newReservationTime = reservationTimeService.createTimeSlot(String.valueOf(reservationTime.getTime()));
        return ResponseEntity.created(URI.create("/times/" + newReservationTime.getId())).body(newReservationTime);
    }

    @GetMapping
    public List<Time> getTimeSlots() {
        return reservationTimeService.getAllTimeSlots();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        reservationTimeService.deleteTimeSlot(id);
        return ResponseEntity.noContent().build();
    }
}
