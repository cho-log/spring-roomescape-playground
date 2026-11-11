
package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import roomescape.ReservationDao;
import roomescape.dto.CreateReservationRequest;
import roomescape.exception.BadRequestReservationException;
import roomescape.exception.ReservationNotFoundException;
import roomescape.model.Reservation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;

@Controller
public class ReservationController {

    private final ReservationDao reservationDao;

    public ReservationController(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }

    private List<Reservation> reservations = new ArrayList<>();

    // 모든 예약 조회 (DAO 사용)
    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> getReservations() {
        return reservationDao.findAll();
    }

    private AtomicLong index = new AtomicLong(1);

    // 예약 생성
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> save(
            @RequestBody Reservation request) {

        if (isBlank(request.getName())
                || isBlank(request.getDate())
                || isBlank(request.getTime())) {
            throw new BadRequestReservationException();
        }

        Reservation saved = reservationDao.save(request);

        return ResponseEntity
                .created(URI.create("/reservations/" + saved.getId()))
                .body(saved);
    }

    // 예약 삭제
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        int deletedRows = reservationDao.delete(id);

        if (deletedRows == 0) {
            throw new ReservationNotFoundException(
                    "존재하지 않는 예약입니다. id=" + id
            );
        }

        return ResponseEntity.noContent().build();
    }
}