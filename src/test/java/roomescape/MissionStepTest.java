package roomescape;

import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "2023-08-05");
        params.put("time", "15:40");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/1")
                .body("id", is(1))
                .body("name", is("브라운"))
                .body("date", is("2023-08-05"))
                .body("time", is("15:40"));

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(1))
                .body("[0].name", is("브라운"));

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
                .when().delete("/reservations/1")
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
        Map<String, String> params = Map.of(
                "name", "브라운", "date", "2023-08-05", "time", "15:40");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .when().delete("/reservations/999")
                .then().log().all()
                .statusCode(404);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(1));

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(404);
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
        RestAssured.given().when().delete("/reservations/999")
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
}
