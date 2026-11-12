package roomescape.controller;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.exception.InvalidReservationException;
import roomescape.exception.NotFoundReservationException;

@RestController // @Controller + @ResponseBody. 모든 메서드의 반환값을 view 이름이 아닌 응답 body(JSON)로 보냄
public class ReservationController {
    private final List<Reservation> reservations = new ArrayList<>();

    private AtomicLong index = new AtomicLong(0);

    public ReservationController() {
        //initializeReservations();
    }

    @GetMapping("/reservations")
    //@ResponseBody //얘는 그냥 반환값을 그대로 응답으로 보내라는거임. 이거 없으면 reservation.html을 찾으려고함.
    //위에 @RestController가 @ResponseBody를 포함하고 있어서 없어도됨.
    public List<Reservation> getReservations() {
        return reservations;
    }


    @PostMapping("/reservations")
    // TS의 Response처럼 HTTP 응답(상태코드, 헤더, 바디)을 담는 클래스. 비동기(Promise)와는 무관
    public ResponseEntity<Reservation> createReservation(
            @RequestBody ReservationRequest request) { // body로 들어온 요청 JSON을 DTO(ReservationRequest)로 받고 request에 넣어줌. //이미 여기서값이 넣어지는것
        if (isBlank(request.name()) || isBlank(request.date()) || isBlank(request.time())) {
            throw new InvalidReservationException("예약 정보(name, date, time)는 비어 있을 수 없습니다.");
        }
        Reservation reservation = new Reservation(index.incrementAndGet(), request.name(), request.date(),
                request.time()); // 요청에는 id가 없으므로, 서버에서 id를 발급해 완성된 Reservation을 만든다
        reservations.add(reservation); //reservations 리스트에 reservation 객체를 추가함.
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(reservation); //201 Created 상태코드와 Location 헤더를 설정하고, body에 reservation 객체를 담아서 반환함.
        //getter를 사용하여 .id 가 아니라 .getId()로 접근해야함.
        // res.status(201).location(`/reservations/${id}`).json(reservation);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();   // null 먼저 검사 (null.isBlank()는 NullPointerException)
    }

    //nestjs에서는 @Delete(:id)
    @DeleteMapping("/reservations/{id}")
    //nestjs에서 @Param()이 spring에서는 @PathVariable()
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        boolean removed = reservations.removeIf(
                reservation -> reservation.getId().equals(id)); //reservations 리스트에서 id가 일치하는 reservation 객체를 제거함.
        if (!removed) {
            throw new NotFoundReservationException("예약 ID " + id + "인 예약을 찾을 수 없습니다.");
        }
        return ResponseEntity.noContent().build(); //204 No Content 상태코드를 반환함.
    }

}
