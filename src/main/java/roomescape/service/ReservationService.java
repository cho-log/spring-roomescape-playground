package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.repository.ReservationJdbcRepository;
import roomescape.repository.ReservationRepository;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.exception.BadRequestException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationJdbcRepository reservationJdbcRepository;
    public List<ReservationResponse> findAllReservations() {
        List<Reservation> reservations = reservationJdbcRepository.findAll();
        List<ReservationResponse> responseDtoList = reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        return responseDtoList;
    }

    public ReservationResponse addReservation(ReservationRequest reservationRequest){
        String name = reservationRequest.getName();
        LocalDate date = reservationRequest.getDate();
        LocalTime time = reservationRequest.getTime();

        Reservation reservation = new Reservation();
        reservation.setName(name);
        reservation.setDate(date);
        reservation.setTime(time);

        Reservation savedReservation = reservationJdbcRepository.save(reservation);

        return ReservationResponse.from(savedReservation);
    }

    public void cancelReservation(Long id) {
        ReservationResponse reservationResponse = findById(id);
        reservationJdbcRepository.deleteById(reservationResponse.getId());
    }

    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationJdbcRepository.findById(id);
        if(reservation == null) {
            throw new BadRequestException("예약을 찾을 수 없습니다. (id=" + id + ")");
        }
        return ReservationResponse.from(reservation);
    }
}
