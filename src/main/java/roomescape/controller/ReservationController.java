package roomescape.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.CreateReservationRequest;
import roomescape.exception.ReservationNotFoundException;
import roomescape.model.Reservation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ReservationController {
    

    // Reservation
    @GetMapping("/reservation")
    public String reservation(){
        return "reservation";
    }

    private List<Reservation> reservations = new ArrayList<>();
    private final AtomicLong index=new AtomicLong(1);
    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> getReservations(Model model){
        //m2 더미데이터 모두 삭제함. 예약 추가 api 만들 때 중복 id 문제 발생 가능성
        return reservations;
    }

    //m3,  예약 추가 api
    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Reservation> createReservation(
           @Valid @RequestBody CreateReservationRequest request
    ){

        Reservation reservation = new Reservation(
                index.getAndIncrement(),
                request.name(),
                request.date(),
                request.time()
        );
        reservations.add(reservation);
        return ResponseEntity
                .created(URI.create("/reservations/"+reservation.getId()))
                .body(reservation);
    }

    //m3 예약 취소 api
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id){
        boolean removed=reservations.removeIf(
                reservation ->
                        reservation.getId().equals(id)
        );
        if(!removed){
            throw new ReservationNotFoundException("존재하지 않는 예약입니다. id="+id);
        }

        return ResponseEntity.noContent().build();
    }
}
