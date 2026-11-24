package roomescape.repository;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.*;

@JdbcTest
class ReservationSchemaTest {
    @Autowired JdbcTemplate jdbcTemplate;

    @ParameterizedTest
    @ValueSource(strings = {"name", "date", "time"})
    void rejectsMissingRequiredValues(String column) {
        Object name = column.equals("name") ? null : "브라운";
        Object date = column.equals("date") ? null : "2023-08-05";
        Object time = column.equals("time") ? null : "15:40";
        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)", name, date, time))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
