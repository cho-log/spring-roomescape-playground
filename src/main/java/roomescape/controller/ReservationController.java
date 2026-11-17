package roomescape.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;
import roomescape.model.ReservationDTO;
import roomescape.repository.ReservationRepository;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class ReservationController {

    private final ReservationRepository reservationRepository;

    @ResponseBody
    @GetMapping("/reservations")
    public List<ReservationDTO> allReservationsController() {
        List<ReservationDTO> reservations = reservationRepository.findAll();
        log.info("reservations = {}", reservations);
        return reservations;
    }

    @GetMapping("/reservation")
    public String reservationController() {
        return "reservation";
    }

    @ResponseBody
    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> reservationAddController(@Valid @RequestBody ReservationDTO reservationDTO) {
        ReservationDTO responseDTO = reservationRepository.reservationAdd(reservationDTO);
        HttpHeaders headers = new HttpHeaders();
        String uri = "/reservations/" + responseDTO.getId();
        headers.setLocation(URI.create(uri));
        ResponseEntity<ReservationDTO> response = new ResponseEntity<>(responseDTO, headers, HttpStatus.CREATED);
        log.info("memberDTO = {}", reservationDTO);
        return response;
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteController(@PathVariable int id) {
        reservationRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity handleException(NoHandlerFoundException e) {
        return ResponseEntity.badRequest().build();
    }
}
