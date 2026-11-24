package roomescape.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDetails;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;
import roomescape.repository.ReservationRepository;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {
    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2023-08-05T06:40:00Z"), ZoneId.of("Asia/Seoul"));
    private final ReservationService service = new ReservationService(repository, clock);

    @Test
    void rejectsPastBookingsBeforeCallingTheRepository() {
        assertThatThrownBy(() -> service.create(new ReservationDetails("브라운", "2023-08-05", "15:39")))
                .isInstanceOfSatisfying(ReservationException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAST_RESERVATION));
        verifyNoInteractions(repository);
    }

    @Test
    void savesFutureBookingsUsingTheConfiguredClock() {
        ReservationDetails details = new ReservationDetails("브라운", "2023-08-05", "15:41");
        Reservation saved = new Reservation(42L, details);
        when(repository.save(details)).thenReturn(saved);
        assertThat(service.create(details)).isSameAs(saved);
    }

    @Test
    void reportsMissingReservationsWhenDeletionReturnsFalse() {
        when(repository.deleteById(42L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteById(42L))
                .isInstanceOfSatisfying(ReservationException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESERVATION_NOT_FOUND));
    }

    @Test
    void deletesAnExistingReservation() {
        when(repository.deleteById(42L)).thenReturn(true);
        assertThatCode(() -> service.deleteById(42L)).doesNotThrowAnyException();
    }
}
