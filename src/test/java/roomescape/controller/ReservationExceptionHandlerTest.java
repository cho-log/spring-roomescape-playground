package roomescape.controller;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ReservationExceptionHandlerTest {
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new ProbeController())
            .setControllerAdvice(new ReservationExceptionHandler()).build();

    @Test
    void handlesExpectedRequestErrors() throws Exception {
        mvc.perform(get("/probe/invalid")).andExpect(status().isBadRequest());
        mvc.perform(get("/probe/missing")).andExpect(status().isNotFound());
    }

    @Test
    void doesNotTreatInternalArgumentErrorsAsClientErrors() {
        assertThatThrownBy(() -> mvc.perform(get("/probe/internal")))
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doesNotTreatDatabaseFailuresAsClientErrors() throws Exception {
        mvc.perform(get("/probe/database")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @Test
    void doesNotTreatStoredDataValidationFailuresAsClientErrors() throws Exception {
        mvc.perform(get("/probe/stored-data")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @RestController
    static class ProbeController {
        @GetMapping("/probe/invalid")
        public void invalid() { throw new ReservationException(ErrorCode.MISSING_NAME); }

        @GetMapping("/probe/missing")
        public void missing() { throw new ReservationException(ErrorCode.RESERVATION_NOT_FOUND); }

        @GetMapping("/probe/stored-data")
        public void storedData() {
            throw new DataRetrievalFailureException("invalid stored date", new ReservationException(ErrorCode.INVALID_DATE));
        }

        @GetMapping("/probe/database")
        public void database() { throw new CannotGetJdbcConnectionException("connection unavailable"); }

        @GetMapping("/probe/internal")
        public void internal() { throw new IllegalArgumentException("internal failure"); }
    }
}
