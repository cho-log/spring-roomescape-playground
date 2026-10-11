package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import roomescape.dto.CreateReservationRequest;
import roomescape.exception.ReservationNotFoundException;
import roomescape.model.Reservation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.validation.Valid;

@Controller
public class ReservationController {

    @GetMapping("/reservation")
    public String reservation(){
        return "reservation";
    }

    private List<Reservation> reservations=new ArrayList<>();

    @GetMapping("/reservations")
    @ResponseBody // List<Reservation>을 JSON 응답으로 반환
    public List<Reservation> getReservations(Model model){

        return reservations;

    }

    private AtomicLong index = new AtomicLong(1);

    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Reservation> createReservation(
            @Valid @RequestBody CreateReservationRequest request
    ){
        Reservation reservation=new Reservation(
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
