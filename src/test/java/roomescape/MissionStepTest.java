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
    void 사단계() {
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

        // 삭제할 예약이 없는 경우
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(404);
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
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운", "2023-08-05", "15:40"
        );

        List<Reservation> reservations = RestAssured.given()
                .when().get("/reservations")
                .then().statusCode(200)
                .extract().jsonPath().getList(".", Reservation.class);

        assertThat(reservations).hasSize(1);

        Reservation reservation = reservations.get(0);

        assertThat(reservation.getName()).isEqualTo("브라운");
        assertThat(reservation.getDate()).isEqualTo("2023-08-05");
        assertThat(reservation.getTime()).isEqualTo("15:40");
    }

    @Test
    void 칠단계() {

        // 첫 번째 예약 생성
        Map<String, String> firstParams = Map.of(
                "name", "브라운",
                "date", "2023-08-05",
                "time", "10:00"
        );

        String firstLocation = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(firstParams)
                .when().post("/reservations")
                .then().statusCode(201)
                .extract().header("Location");


        // 두 번째 예약 생성
        Map<String, String> secondParams = Map.of(
                "name", "토미",
                "date", "2023-08-06",
                "time", "11:00"
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(secondParams)
                .when().post("/reservations")
                .then().statusCode(201);


        // 예약 두 건이 저장되었는지 확인
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(1) FROM reservation",
                Integer.class
        );

        assertThat(count).isEqualTo(2);


        // 첫 번째 예약만 삭제
        RestAssured.given()
                .when().delete(firstLocation)
                .then().statusCode(204);


        // 두 번째 예약만 남았는지 확인
        List<Reservation> remainingReservations = RestAssured.given()
                .when().get("/reservations")
                .then().statusCode(200)
                .extract().jsonPath().getList(".", Reservation.class);

        assertThat(remainingReservations).hasSize(1);

        Reservation remaining = remainingReservations.get(0);

        assertThat(remaining.getName()).isEqualTo("토미");
        assertThat(remaining.getDate()).isEqualTo("2023-08-06");
        assertThat(remaining.getTime()).isEqualTo("11:00");


        // DB에도 한 건만 남았는지 확인
        Integer countAfterDelete = jdbcTemplate.queryForObject(
                "SELECT count(1) FROM reservation",
                Integer.class
        );

        assertThat(countAfterDelete).isEqualTo(1);
    }

    @Test
    void 예약자_이름_길이_검증() {

        // 255자 이름: 성공
        String name255 = "가".repeat(255);

        Map<String, String> params255 = Map.of(
                "name", name255,
                "date", "2023-08-05",
                "time", "10:00"
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params255)
                .when().post("/reservations")
                .then().statusCode(201);


        // 256자 이름: 실패
        String name256 = "가".repeat(256);

        Map<String, String> params256 = Map.of(
                "name", name256,
                "date", "2023-08-05",
                "time", "11:00"
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



}
