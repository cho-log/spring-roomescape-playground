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

@Controller
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();

    private AtomicLong index = new AtomicLong(1);

    public ReservationController() {
        //initializeReservations();
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation"; //reservation.html을 찾아서 렌더링해라. thymeleaf가 알아서 해줌.
    }


    @GetMapping("reservations")
    @ResponseBody //얘는 그냥 반환값을 그대로 응답으로 보내라는거임. 이거 없으면 reservation.html을 찾으려고함.
    public List<Reservation> getReservations() {
        return reservations;
    }


    @PostMapping("/reservations")
    //이거 TS로 치면 Promise<Reservation> 뭐 이런느낌?
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) { //body에 들어오는 json을 Reservation 객체로 바꿔서 reservation에 넣어줌. //이미 여기서값이 넣어지는것
        reservation.setId(index.getAndIncrement()); //reservation 객체에 id를 설정해줌. index는 1부터 시작해서 1씩 증가함.
        reservations.add(reservation); //reservations 리스트에 reservation 객체를 추가함.
        return ResponseEntity.created(URI.create("/reservations/" + reservation.id)).body(reservation); //201 Created 상태코드와 Location 헤더를 설정하고, body에 reservation 객체를 담아서 반환함.
        // res.status(201).location(`/reservations/${id}`).json(reservation);
    } //아 잠만 이래서 dto 쓰는건가? 뭔가 하나 빈상태로 넣네 질문해야지


    //nestjs에서는 @Delete(:id)
    @DeleteMapping("/reservations/{id}")
    //nestjs에서 @Param()이 spring에서는 @PathVariable()
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservations.removeIf(reservation -> reservation.id.equals(id)); //reservations 리스트에서 id가 일치하는 reservation 객체를 제거함.
        return ResponseEntity.noContent().build(); //204 No Content 상태코드를 반환함.
    }

}
