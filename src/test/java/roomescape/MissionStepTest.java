package roomescape;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.tuple;
import java.sql.Connection;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import roomescape.domain.Reservation;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {

    @Test
    void first() {
        RestAssured.given().log().all()
                .when().get("/")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void second() {
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
    void third() {
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
                .body("[0].name", is("브라운"))
                .body("[0].date", is("2023-08-05"))
                .body("[0].time", is("15:40"));

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
    void fourth() {
        // 필요한 인자가 없는 경우
        assertBadRequest("브라운", "", "15:40");
        assertBadRequest("브라운", "2023-08-05", "");
        assertBadRequest("a".repeat(256), "2023-08-05", "15:40");


        // 존재하지 않는 날짜/시간인 경우
        assertBadRequest("브라운", "2023-02-30", "15:40");
        assertBadRequest("브라운", "2023-08-05", "abc");

        // 삭제할 예약이 없는 경우
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void reservation255() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "a".repeat(255));
        params.put("date", "2023-08-05");
        params.put("time", "15:40");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then()
                .statusCode(201);
    }

    private void assertBadRequest(String name, String date, String time) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("date", date);
        params.put("time", time);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void fifth() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null).next()).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void reservationsFromDatabase() {
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운", "2023-08-05", "15:40"
        );
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "블랙", "2023-08-06", "15:40"
        );

        List<Reservation> reservations = RestAssured.given()
                .when().get("/reservations")
                .then().statusCode(200)
                .extract().jsonPath().getList(".", Reservation.class);

        assertThat(reservations)
                .extracting(
                        Reservation::getId,
                        Reservation::getName,
                        Reservation::getDate,
                        Reservation::getTime
                )
                .containsExactlyInAnyOrder(
                        tuple(1L, "브라운", "2023-08-05", "15:40"),
                        tuple(2L, "블랙", "2023-08-06", "15:40")
                );
    }

    @Test
    void reservationInDatabase() {
        jdbcTemplate.update("INSERT INTO reservation (name, date, time) VALUES(?, ?, ?)", "기존 예약", "2023-08-04", "09:00" );
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "2023-08-05");
        params.put("time", "10:00");

        String location = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        long createdId = Long.parseLong(
                location.substring(location.lastIndexOf("/") + 1));

        Integer countAfterCreate = jdbcTemplate.queryForObject(
                "SELECT count(1) FROM reservation",
                Integer.class);
        assertThat(countAfterCreate).isEqualTo(2);


        String createdName = jdbcTemplate.queryForObject(
                "SELECT name FROM reservation WHERE id = ?",
                String.class,
                createdId);
        assertThat(createdName).isEqualTo("브라운");


        RestAssured.given().when().delete(location).then().statusCode(204);


        Integer countAfterDelete = jdbcTemplate.queryForObject(
                "SELECT count(1) FROM reservation",
                Integer.class);
        assertThat(countAfterDelete).isEqualTo(1);

        String remainingName = jdbcTemplate.queryForObject(
                "SELECT name FROM reservation",
                String.class);
        assertThat(remainingName).isEqualTo("기존 예약");
    }
}

