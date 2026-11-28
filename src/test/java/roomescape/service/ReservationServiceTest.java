package roomescape.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.exception.PastReservationException;
import roomescape.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void 미래_예약을_생성한다() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.now();
        Reservation expected =
                Reservation.restore(1L, "brown", date, time);

        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(expected);

        assertThat(
                reservationService.createReservation("brown", date, time)
        ).isSameAs(expected);

        ArgumentCaptor<Reservation> captor =
                ArgumentCaptor.forClass(Reservation.class);

        verify(reservationRepository).save(captor.capture());

        Reservation actual = captor.getValue();

        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo("brown");
        assertThat(actual.getDate()).isEqualTo(date);
        assertThat(actual.getTime()).isEqualTo(time);
    }

    @Test
    void 과거_날짜의_예약은_생성할_수_없다() {
        LocalDate date = LocalDate.now().minusDays(1);
        LocalTime time = LocalTime.now();

        assertThatThrownBy(() -> reservationService.createReservation("brown", date, time))
                .isInstanceOf(PastReservationException.class)
                .hasMessage("과거 예약 시도: " + LocalDateTime.of(date, time));

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void 오늘_이미_지난_시간의_예약은_생성할_수_없다() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.MIDNIGHT;
        ReservationRequest request = new ReservationRequest(
                "brown",
                date,
                time
        );

        assertThatThrownBy(() -> reservationService.createReservation("brown", date, time))
                .isInstanceOf(PastReservationException.class)
                .hasMessage("과거 예약 시도: " + LocalDateTime.of(date, time));

        verifyNoInteractions(reservationRepository);
    }
}
