package roomescape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.ReservationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(MissionStepTest.FixedClockConfiguration.class)
@Sql(statements = "DELETE FROM reservation")
public class MissionStepTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @TestConfiguration
    static class FixedClockConfiguration {
        @Bean
        @Primary
        Clock testClock() {
            return Clock.fixed(Instant.parse("2023-08-01T00:00:00Z"), ZoneId.of("Asia/Seoul"));
        }
    }

    @Test
    void 일단계() {
        RestAssured.given().log().all()
                .when().get("/")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 이단계() {
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
    void 삼단계() {
        Response response = createReservation(Map.of("name", "브라운", "date", "2023-08-05", "time", "15:40"));
        response.then().statusCode(201);
        ReservationResponse saved = response.as(ReservationResponse.class);
        assertThat(saved.id()).isPositive();
        response.then().header("Location", "/reservations/" + saved.id());
        assertThat(saved).isEqualTo(new ReservationResponse(saved.id(), "브라운", "2023-08-05", "15:40"));

        List<ReservationResponse> reservations = RestAssured.given().when().get("/reservations")
                .then().statusCode(200).extract().jsonPath().getList(".", ReservationResponse.class);
        assertThat(reservations).containsExactly(saved);
        RestAssured.given().when().delete("/reservations/" + saved.id()).then().statusCode(204);
        RestAssured.given().when().get("/reservations")
                .then().statusCode(200).body("size()", is(0));
    }

    @Test
    void 사단계() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "");
        params.put("time", "");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));

        RestAssured.given().log().all()
                .when().delete("/reservations/" + Long.MAX_VALUE)
                .then().log().all()
                .statusCode(404);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void 필수값이_null이거나_공백이면_400을_반환한다(String value) {
        for (String field : List.of("name", "date", "time")) {
            Map<String, String> params = new HashMap<>(Map.of(
                    "name", "브라운", "date", "2023-08-05", "time", "15:40"));
            params.put(field, value);

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "date", "time"})
    void 필수값이_누락되면_400을_반환한다(String field) {
        Map<String, String> params = new HashMap<>(Map.of(
                "name", "브라운", "date", "2023-08-05", "time", "15:40"));
        params.remove(field);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void 없는_예약을_삭제해도_기존_예약은_유지된다() {
        ReservationResponse saved = createReservation(Map.of(
                "name", "브라운", "date", "2023-08-05", "time", "15:40"))
                .then().statusCode(201).extract().as(ReservationResponse.class);
        RestAssured.given().when().delete("/reservations/" + Long.MAX_VALUE).then().statusCode(404);
        List<ReservationResponse> remaining = RestAssured.given().when().get("/reservations")
                .then().statusCode(200).extract().jsonPath().getList(".", ReservationResponse.class);
        assertThat(remaining).containsExactly(saved);
        RestAssured.given().when().delete("/reservations/" + saved.id()).then().statusCode(204);
        RestAssured.given().when().delete("/reservations/" + saved.id()).then().statusCode(404);
    }

    @ParameterizedTest
    @CsvSource({"date, 2026-02-30, INVALID_DATE", "time, 25:00, INVALID_TIME"})
    void 존재하지_않는_날짜와_시간은_저장하지_않는다(String field, String value, String expectedCode) {
        Map<String, String> params = new HashMap<>(Map.of(
                "name", "브라운", "date", "2023-08-05", "time", "15:40"));
        params.put(field, value);
        RestAssured.given().contentType(ContentType.JSON).body(params)
                .when().post("/reservations")
                .then().statusCode(400).body("code", is(expectedCode));
        RestAssured.given().when().get("/reservations")
                .then().statusCode(200).body("size()", is(0));
    }

    @Test
    void 예약_오류의_코드와_메시지를_반환한다() {
        RestAssured.given().contentType(ContentType.JSON)
                .body(Map.of("name", "", "date", "2023-08-05", "time", "15:40"))
                .when().post("/reservations")
                .then().statusCode(400).body("code", is("MISSING_NAME"))
                .body("message", is("예약자 이름은 필수입니다."));
        RestAssured.given().when().delete("/reservations/" + Long.MAX_VALUE)
                .then().statusCode(404).body("code", is("RESERVATION_NOT_FOUND"))
                .body("message", is("예약을 찾을 수 없습니다."));
    }

    @Test
    void 잘못된_JSON은_요청_오류로_안내한다() {
        RestAssured.given().contentType(ContentType.JSON).body("{broken")
                .when().post("/reservations")
                .then().statusCode(400).body("code", is("INVALID_REQUEST"));
    }

    @Test
    void 오단계() throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             ResultSet tables = connection.getMetaData().getTables(null, null, "RESERVATION", null)) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(tables.next()).isTrue();
        }
    }

    @Test
    void 육단계() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운", "2023-08-05", "15:40");
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class);
        List<ReservationResponse> reservations = RestAssured.given().when().get("/reservations")
                .then().statusCode(200).extract().jsonPath().getList(".", ReservationResponse.class);
        assertThat(reservations).hasSize(count);
    }

    @Test
    void DB에서_조회한_예약의_이름과_날짜와_시간을_응답한다() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운", "2023-08-05", "15:40");
        RestAssured.given().when().get("/reservations").then().statusCode(200)
                .body("[0].name", is("브라운"))
                .body("[0].date", is("2023-08-05"))
                .body("[0].time", is("15:40"));
    }

    @Test
    void 칠단계() {
        Response response = createReservation(Map.of("name", "브라운", "date", "2023-08-05", "time", "10:00"));
        ReservationResponse saved = response.then().statusCode(201).extract().as(ReservationResponse.class);
        response.then().header("Location", "/reservations/" + saved.id());
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT name FROM reservation WHERE id = ?", String.class, saved.id()))
                .isEqualTo("브라운");
        RestAssured.given().when().delete("/reservations/" + saved.id()).then().statusCode(204);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isZero();
    }

    @Test
    void DB에_기존_예약이_있으면_새로_생성한_id를_응답한다() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "기존 예약", "2023-08-05", "10:00");
        Long existingId = jdbcTemplate.queryForObject("SELECT id FROM reservation", Long.class);
        Response response = createReservation(Map.of("name", "새 예약", "date", "2023-08-05", "time", "15:40"));
        ReservationResponse saved = response.then().statusCode(201).extract().as(ReservationResponse.class);
        assertThat(saved.id()).isPositive().isNotEqualTo(existingId);
        response.then().header("Location", "/reservations/" + saved.id());
        assertThat(jdbcTemplate.queryForObject("SELECT name FROM reservation WHERE id = ?", String.class, saved.id()))
                .isEqualTo("새 예약");
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isEqualTo(2);
    }

    @Test
    void 잘못된_예약_요청은_DB에_저장되지_않는다() {
        RestAssured.given().contentType(ContentType.JSON)
                .body(Map.of("name", "", "date", "2023-08-05", "time", "15:40"))
                .when().post("/reservations")
                .then().statusCode(400).body("code", is("MISSING_NAME"));
        RestAssured.given().contentType(ContentType.JSON)
                .body(Map.of("name", "브라운", "date", "2026-02-30", "time", "15:40"))
                .when().post("/reservations")
                .then().statusCode(400).body("code", is("INVALID_DATE"));
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isZero();
    }
    @Test
    void 이름이_255자면_허용하고_256자면_400을_반환한다() {
        createReservation(Map.of("name", "가".repeat(255), "date", "2023-08-05", "time", "15:40"))
                .then().statusCode(201);
        createReservation(Map.of("name", "가".repeat(256), "date", "2023-08-05", "time", "16:40"))
                .then().statusCode(400).body("code", is("NAME_TOO_LONG"));
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({"2000-01-01, 15:40", "2023-08-01, 08:59", "2023-08-01, 09:00"})
    void 과거와_현재_시각으로_새_예약을_만들_수_없다(String date, String time) {
        createReservation(Map.of("name", "브라운", "date", date, "time", time))
                .then().statusCode(400).body("code", is("PAST_RESERVATION"));
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"15:40", "15:40:00", "15:40:00.000"})
    void 같은_날짜와_시각의_중복_예약은_409를_반환한다(String time) {
        createReservation(Map.of("name", "첫번째", "date", "2023-08-05", "time", "15:40"))
                .then().statusCode(201);
        createReservation(Map.of("name", "두번째", "date", "2023-08-05", "time", time))
                .then().statusCode(409).body("code", is("DUPLICATE_RESERVATION"));
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isEqualTo(1);
    }

    @Test
    void 동시에_같은_예약을_생성해도_하나만_저장된다() throws Exception {
        var executor = Executors.newFixedThreadPool(2);
        CyclicBarrier start = new CyclicBarrier(2);
        Callable<Integer> request = () -> {
            start.await(5, TimeUnit.SECONDS);
            return createReservation(Map.of("name", "브라운", "date", "2023-08-05", "time", "15:40")).statusCode();
        };
        try {
            var first = executor.submit(request);
            var second = executor.submit(request);
            assertThat(List.of(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(201, 409);
            assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "date", "time"})
    void 저장된_데이터의_오류는_입력_오류가_아닌_500을_반환한다(String field) {
        Map<String, String> values = new HashMap<>(Map.of(
                "name", "브라운", "date", "2023-08-05", "time", "15:40"));
        values.put(field, switch (field) { case "name" -> " "; case "date" -> "2023/08/05"; default -> "25:00"; });
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                values.get("name"), values.get("date"), values.get("time"));
        RestAssured.given().when().get("/reservations").then().statusCode(500).body("code", is("INTERNAL_ERROR"));
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM reservation", Integer.class)).isEqualTo(1);
    }

    @Test
    void 과거에_저장된_예약은_조회할_수_있다() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운", "2000-01-01", "15:40");
        RestAssured.given().when().get("/reservations").then().statusCode(200)
                .body("[0].date", is("2000-01-01"));
    }

    private Response createReservation(Map<String, String> params) {
        return RestAssured.given().contentType(ContentType.JSON).body(params).when().post("/reservations");
    }
}
