package roomescape;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Connection;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import roomescape.controller.ReservationController;
import java.lang.reflect.Field;


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
    void reservationUsesRegisteredTime() {
        Long timeId = saveTime("15:40");
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "2023-08-05");
        params.put("time", timeId.toString());

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
                .body("time.id", is(timeId.intValue()))
                .body("time.time", is("15:40"));

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is("브라운"))
                .body("[0].date", is("2023-08-05"))
                .body("[0].time.id", is(timeId.intValue()))
                .body("[0].time.time", is("15:40"));

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
    void invalidReservationIsNotSaved() {
        String timeId = saveTime("15:40").toString();

        assertBadRequest("", "2023-08-05", timeId);

        assertBadRequest("a".repeat(256), "2023-08-05", timeId);

        assertBadRequest("브라운", "", timeId);
        assertBadRequest("브라운", "2023-02-30", timeId);

        assertBadRequest("브라운", "2023-08-05", "");
        assertBadRequest("브라운", "2023-08-05", "abc");
        assertBadRequest("브라운", "2023-08-05", "999");

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void nameWith255CharactersIsPreserved() {
        Long timeId = saveTime("15:40");
        String name = "a".repeat(255);

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("date", "2023-08-05");
        params.put("time", timeId.toString());

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", is(name));
    }

    private void assertBadRequest(String name, String date, String time) {
        Long countBefore = reservationCount();

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

        assertThat(reservationCount()).isEqualTo(countBefore);
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
        Long firstTimeId = saveTime("15:40");
        Long secondTimeId = saveTime("16:40");
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time_id) VALUES (?, ?, ?)",
                "브라운", "2023-08-05", firstTimeId
        );
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time_id) VALUES (?, ?, ?)",
                "블랙", "2023-08-06", secondTimeId
        );

        RestAssured.given()
                .when().get("/reservations")
                .then().statusCode(200)
                .body("size()", is(2))
                .body("find { it.id == 1 }.name", is("브라운"))
                .body("find { it.id == 1 }.date", is("2023-08-05"))
                .body("find { it.id == 1 }.time.id", is(firstTimeId.intValue()))
                .body("find { it.id == 1 }.time.time", is("15:40"))
                .body("find { it.id == 2 }.name", is("블랙"))
                .body("find { it.id == 2 }.date", is("2023-08-06"))
                .body("find { it.id == 2 }.time.id", is(secondTimeId.intValue()))
                .body("find { it.id == 2 }.time.time", is("16:40"));
    }

    @Test
    void reservationInDatabase() {
        Long existingTimeId = saveTime("09:00");
        Long newTimeId = saveTime("10:00");
        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id) VALUES(?, ?, ?)",
                "기존 예약", "2023-08-04", existingTimeId);
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "2023-08-05");
        params.put("time", newTimeId.toString());

        String location = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then()
                .statusCode(201)
                .body("time.id", is(newTimeId.intValue()))
                .body("time.time", is("10:00"))
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
        Long createdTimeId = jdbcTemplate.queryForObject(
                "SELECT time_id FROM reservation WHERE id = ?", Long.class, createdId);
        assertThat(createdTimeId).isEqualTo(newTimeId);


        RestAssured.given().when().delete(location).then().statusCode(204);


        Integer countAfterDelete = jdbcTemplate.queryForObject(
                "SELECT count(1) FROM reservation",
                Integer.class);
        assertThat(countAfterDelete).isEqualTo(1);

        String remainingName = jdbcTemplate.queryForObject(
                "SELECT name FROM reservation",
                String.class);
        assertThat(remainingName).isEqualTo("기존 예약");
        assertThat(jdbcTemplate.queryForObject("SELECT time_id FROM reservation", Long.class))
                .isEqualTo(existingTimeId);
    }

    @Test
    void timeCanBeCreatedListedAndDeleted() {
        Map<String, String> params = new HashMap<>();
        params.put("time", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/times/1")
                .body("id", is(1))
                .body("time", is("10:00"));

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(1))
                .body("[0].time", is("10:00"));

        RestAssured.given().log().all()
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(204);
        RestAssured.given()
                .when().get("/times")
                .then().statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void reservationWithTimeStringIsRejected() {
        Long countBefore = reservationCount();
        Map<String, String> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2023-08-05");
        reservation.put("time", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
        assertThat(reservationCount()).isEqualTo(countBefore);
    }

    @Autowired
    private ReservationController reservationController;

    @Test
    void reservationControllerDoesNotDependOnJdbcTemplate() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        assertThat(isJdbcTemplateInjected).isFalse();
    }
    private Long saveTime(String value) {
        jdbcTemplate.update(
                "INSERT INTO time (time) VALUES (?)",
                value
        );

        return jdbcTemplate.queryForObject(
                "SELECT id FROM time WHERE time = ?",
                Long.class,
                value
        );
    }

    private Long reservationCount() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reservation",
                Long.class
        );
    }
    @Test
    void deleteMissingReservationKeepsExisting() {
        Long timeId = saveTime("15:40");

        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time_id) VALUES (?, ?, ?)",
                "브라운", "2023-08-05", timeId
        );

        Long reservationId = jdbcTemplate.queryForObject(
                "SELECT id FROM reservation WHERE name = ?",
                Long.class,
                "브라운"
        );

        RestAssured.given().log().all()
                .when().delete("/reservations/" + (reservationId + 1))
                .then().log().all()
                .statusCode(400);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(reservationId.intValue()))
                .body("[0].name", is("브라운"))
                .body("[0].date", is("2023-08-05"))
                .body("[0].time.id", is(timeId.intValue()))
                .body("[0].time.time", is("15:40"));
    }
}

