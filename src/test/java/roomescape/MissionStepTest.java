package roomescape;

import io.restassured.RestAssured;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import static org.hamcrest.Matchers.is;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import io.restassured.http.ContentType;
import roomescape.model.Reservation;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {

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
                .body("size()", is(0)); // 아직 생성 요청이 없으니 Controller에서 임의로 넣어준 Reservation 갯수 만큼 검증하거나 0개임을 확인하세요.
    }

    @Test
    void 삼단계() {
        long timeId = createTime("15:40");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "브라운", "date", "2023-08-05", "time", timeId))
                .when().post("/reservations")
                .then().statusCode(201)
                .extract().response();

        long reservationId = response.jsonPath().getLong("id");
        String location = response.getHeader("Location");
        assertThat(location).isEqualTo("/reservations/" + reservationId);

        RestAssured.given()
                .when().get("/reservations")
                .then().statusCode(200)
                .body("size()", is(1));

        RestAssured.given()
                .when().delete(location)
                .then().statusCode(204);

        RestAssured.given()
                .when().get("/reservations")
                .then().statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 사단계() {
        long timeId = createTime("10:00");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "브라운", "date", "", "time", timeId))
                .when().post("/reservations")
                .then().statusCode(400);

        RestAssured.given()
                .when().delete("/reservations/1")
                .then().statusCode(404);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 오단계() throws Exception {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData()
                    .getTables(null, null, "RESERVATION", null).next()).isTrue();
        }
    }

    @Test
    void 육단계() {
        long timeId = createTime("15:40");

        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time_id) VALUES (?, ?, ?)",
                "브라운", "2023-08-05", timeId);

        RestAssured.given()
                .when().get("/reservations")
                .then().statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is("브라운"))
                .body("[0].date", is("2023-08-05"))
                .body("[0].time.id", is(Math.toIntExact(timeId)))
                .body("[0].time.time", is("15:40"));
    }

    @Test
    void 칠단계() {
        long timeId = createTime("10:00");
        Map<String, Object> params = Map.of(
                "name", "브라운", "date", "2023-08-05", "time", timeId);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().statusCode(201)
                .extract().response();

        long id = response.jsonPath().getLong("id");
        String location = response.getHeader("Location");
        assertThat(location).isEqualTo("/reservations/" + id);

        Integer savedCount= jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reservation WHERE id = ? AND name = ? AND date = ? AND time_id = ?",
                Integer.class, id, params.get("name"), params.get("date"), timeId);
        assertThat(savedCount).isEqualTo(1);

        RestAssured.given()
                .when().delete(location)
                .then().statusCode(204);

        Integer countAfterDelete = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reservation", Integer.class);
        assertThat(countAfterDelete).isZero();
    }

    @Test
    void 팔단계() {
        RestAssured.given()
                .when().get("/time")
                .then().statusCode(200);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("time", "10:00"))
                .when().post("/times")
                .then().statusCode(201)
                .extract().response();

        long id = response.jsonPath().getLong("id");
        assertThat(response.getHeader("Location")).isEqualTo("/times/" + id);

        RestAssured.given()
                .when().get("/times")
                .then().statusCode(200)
                .body("size()", is(1))
                .body("[0].time", is("10:00"));

        RestAssured.given()
                .when().delete("/times/{id}", id)
                .then().statusCode(204);

        RestAssured.given()
                .when().get("/times")
                .then().statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 구단계() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "브라운", "date", "2023-08-05", "time", "10:00"))
                .when().post("/reservations")
                .then().statusCode(400);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "브라운", "date", "2023-08-05", "time", 999L))
                .when().post("/reservations")
                .then().statusCode(400);
    }

    @Test
    void 예약자_이름이_저장_한도를_넘으면_400을_반환한다() {
        long timeId = createTime("10:00");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "a".repeat(256), "date", "2023-08-05", "time", timeId))
                .when().post("/reservations")
                .then().statusCode(400);
    }

    @Test
    void 예약자_이름_길이_검증() {
        long timeId = createTime("10:00");

        // 255자 이름: 성공
        String name255 = "가".repeat(255);

        Map<String, Object> params255 = Map.of(
                "name", name255,
                "date", "2023-08-05",
                "time", timeId
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params255)
                .when().post("/reservations")
                .then().statusCode(201);

        // 256자 이름: 실패
        String name256 = "가".repeat(256);

        Map<String, Object> params256 = Map.of(
                "name", name256,
                "date", "2023-08-05",
                "time", timeId
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params256)
                .when().post("/reservations")
                .then().statusCode(400);
    }

    private long createTime(String time) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("time", time))
                .when().post("/times")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }
    
}
