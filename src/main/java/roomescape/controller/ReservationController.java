package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;

@Controller
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();

    private final AtomicLong index = new AtomicLong(1);

    public ReservationController() {
        //initializeReservations();
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation"; //reservation.html을 찾아서 렌더링해라. thymeleaf가 알아서 해줌.
    }


    @GetMapping("reservations")
    @ResponseBody //얘는 그냥 반환값을 그대로 응답으로 보내라는거임. 이거 없으면 reservation.html을 찾으려고함.
    public List<ReservationResponse> getReservations() {
        return reservations.stream().map(ReservationResponse::from).toList();
    }


    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation(index.getAndIncrement(),
                request.name(), request.date(), request.time());
        reservations.add(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(ReservationResponse.from(reservation));
    }


    //nestjs에서는 @Delete(:id)
    @DeleteMapping("/reservations/{id}")
    //nestjs에서 @Param()이 spring에서는 @PathVariable()
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservations.removeIf(reservation -> reservation.hasId(id)); //reservations 리스트에서 id가 일치하는 reservation 객체를 제거함.
        return ResponseEntity.noContent().build(); //204 No Content 상태코드를 반환함.
    }

}
