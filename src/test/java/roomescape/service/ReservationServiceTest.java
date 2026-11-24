package roomescape.service;

import org.junit.jupiter.api.Test;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;
import roomescape.repository.ReservationRepository;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {
    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final ReservationService service = new ReservationService(repository);

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
