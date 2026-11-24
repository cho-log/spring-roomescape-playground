package roomescape;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SimpleDuplicateReservationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 같은_시간으로_동시에_예약해도_하나만_저장된다() throws Exception {
        Map<String, String> params = Map.of("name", "브라운", "date", "2026-12-25", "time", "10:00");

        // 같은 예약 요청을 20개의 스레드에서 동시에 보낸다
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(params)
                    .post("/reservations"));
        }

        // 모든 요청이 끝날 때까지 기다린다
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // 예약은 하나만 저장되어야 하는데 여러 개가 저장된다
        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertThat(count).isEqualTo(1);
    }
}
