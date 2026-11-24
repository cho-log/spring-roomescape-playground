package roomescape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.model.Reservation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {

    @Test
    void 메인_페이지_응답() {
        RestAssured.given().log().all()
                .when().get("/")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 예약_페이지_응답과_목록_조회() {
        RestAssured.given().log().all()
                .when().get("/reservation")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 예약_추가와_삭제() {
        String tomorrow = LocalDate.now().plusDays(1).toString();

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow);
        params.put("time", "15:40");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/1")
                .body("id", is(1));

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 예약_추가_시_인자값이_비어_있으면_400을_응답한다() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "");
        params.put("time", "");

        // 필요한 인자가 없는 경우
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void 존재하지_않는_예약을_삭제하면_404를_응답한다() {
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(404);
    }

    @Test
    void 지난_날짜로_예약하면_400을_응답한다() {
        String yesterday = LocalDate.now().minusDays(1).toString();

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", yesterday);
        params.put("time", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void 지난_시간으로_예약하면_400을_응답한다() {
        LocalDateTime oneSecondAgo = LocalDateTime.now()
                .minusSeconds(1)
                .truncatedTo(ChronoUnit.SECONDS);

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", oneSecondAgo.toLocalDate().toString());
        params.put("time", oneSecondAgo.toLocalTime().toString());

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void 이미_예약된_날짜와_시간으로_예약하면_409를_응답한다() {
        String tomorrow = LocalDate.now().plusDays(1).toString();

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow);
        params.put("time", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(409);
    }

    @Test
    void 같은_시간에_동시에_예약하면_하나만_저장된다() throws InterruptedException {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow);
        params.put("time", "10:00");

        int requestCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch endSignal = new CountDownLatch(requestCount);
        List<Integer> statusCodes = new CopyOnWriteArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try{
                    startSignal.await();
                    int statusCode = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .body(params)
                            .when().post("/reservations")
                            .then().extract().statusCode();
                    statusCodes.add(statusCode);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    endSignal.countDown();
                }
            });
        }
        startSignal.countDown();
        endSignal.await();
        executorService.shutdown();

        assertThat(statusCodes).containsExactlyInAnyOrder(201, 409);
        Integer count = jdbcTemplate.queryForObject("SELECT count(1) FROM reservation", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 오단계() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null).next()).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void 육단계() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)", "브라운", "2023-08-05",
                "15:40");

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(count))
                .body("[0].name", is("브라운"));
    }

    @Test
    void 칠단계() {
        String tomorrow = LocalDate.now().plusDays(1).toString();

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", tomorrow);
        params.put("time", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/1");

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertThat(count).isEqualTo(1);

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        Integer countAfterDelete = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertThat(countAfterDelete).isEqualTo(0);
    }

}
