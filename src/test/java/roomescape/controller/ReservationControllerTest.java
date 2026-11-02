package roomescape.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import roomescape.domain.Time;
import roomescape.repository.ReservationRepository;
import roomescape.repository.TimeRepository;
import roomescape.service.ReservationService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ReservationControllerTest {
    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final TimeRepository timeRepository = mock(TimeRepository.class);
    private final ReservationController controller = new ReservationController(
            new ReservationService(reservationRepository, timeRepository)
    );

    @BeforeEach
    void registerTime() {
        when(timeRepository.findById(1L)).thenReturn(new Time(1L, "15:40"));
    }

    @Test
    void missingNameAndLongNameHaveDifferentMessages() {
        assertInvalid("", "2023-08-05", "1",
                "예약자 이름을 입력해주세요.");
        assertInvalid("a".repeat(256), "2023-08-05", "1",
                "예약자 이름은 255자 이하여야 합니다.");
    }

    @Test
    void missingDateAndTimeHaveSpecificMessages() {
        assertInvalid("브라운", "", "1",
                "예약 날짜를 입력해주세요.");
        assertInvalid("브라운", "2023-08-05", "",
                "예약 시간을 입력해주세요.");
        assertInvalid("브라운", "2023-08-05", null,
                "예약 시간을 입력해주세요.");
    }

    @Test
    void malformedAndUnregisteredTimeIdsHaveDifferentMessages() {
        when(timeRepository.findById(999L))
                .thenThrow(new EmptyResultDataAccessException(1));

        assertInvalid("브라운", "2023-08-05", "15:40",
                "시간 ID 형식이 올바르지 않습니다.");
        assertInvalid("브라운", "2023-08-05", "999",
                "등록되지 않은 시간입니다.");
    }

    private void assertInvalid(String name, String date, String timeId, String message) {
        Map<String, String> request = new HashMap<>();
        request.put("name", name);
        request.put("date", date);
        request.put("time", timeId);

        assertThatThrownBy(() -> controller.addReservation(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
        verifyNoInteractions(reservationRepository);
    }
}
