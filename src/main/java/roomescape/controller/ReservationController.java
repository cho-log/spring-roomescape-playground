package roomescape.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.Reservation;


import roomescape.domain.Time;
import roomescape.domain.dto.ReservationDto;
import roomescape.repository.ReservationDao;
import roomescape.repository.TimeDao;
import roomescape.valid.ErrorCode;
import roomescape.valid.exception.IllegalReservationException;
import roomescape.valid.exception.NotFoundReservationException;

import java.net.URI;
import java.util.*;

@Slf4j
@RestController
public class ReservationController {
    private final ReservationDao reservationDao;

    private final TimeDao timeDao;

    @Autowired
    public ReservationController(ReservationDao reservationDao, TimeDao timeDao) {
        this.reservationDao = reservationDao;
        this.timeDao = timeDao;
    }

    @GetMapping("/reservations")
    public List<Reservation> reservations() {
        return reservationDao.findAllReservation();
    }

    

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> reserve(@Valid @RequestBody ReservationDto reservationDto,
                                               BindingResult bindingResult) throws IllegalReservationException {
        if(bindingResult.hasErrors()) {
            throw new IllegalReservationException(ErrorCode.ILLEGAL_ARGUMENT);
        }


        log.info("reservation timeId = {}", reservationDto.getTime());
        Time time = timeDao.findById(reservationDto.getTime());

        Reservation reservation = Reservation.builder()
                .name(reservationDto.getName())
                .time(time)
                .date(reservationDto.getDate())
                .build();



        Long id = reservationDao.save(reservation);
        Reservation newReservation = Reservation.toEntity(reservation, id);
        return ResponseEntity.created(URI.create("/reservations/" + id)).body(newReservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> rejectReserve(@PathVariable Long id) throws NotFoundReservationException {
        reservationDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({NotFoundReservationException.class, IllegalReservationException.class})
    public ResponseEntity<Void> handleException(Exception e) {
        log.info("error message = {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
