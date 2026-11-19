package roomescape.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.NotFoundReservationException;
import roomescape.exception.PastReservationException;
import roomescape.model.Reservation;
import roomescape.repository.ReservationRepository;

@Service
public class ReservationService {
    private  final ReservationRepository reservationRepository;
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse create(ReservationRequest reservationRequest) {
        Reservation reservation = reservationRequest.toEntity();

        if (LocalDateTime.of(reservation.getDate(), reservation.getTime()).isBefore(LocalDateTime.now())) {
            throw new PastReservationException("지난 날짜와 시간으로는 예약할 수 없습니다.");
        }

        if (reservationRepository.existsByDateAndTime(reservation.getDate(), reservation.getTime())) {
            throw new DuplicateReservationException("이미 예약된 날짜와 시간입니다.");
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    public void delete(long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundReservationException("예약이 없습니다."));
        reservationRepository.delete(reservation);
    }
}
