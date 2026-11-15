package roomescape.controller;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationApiTest {

    @Test
    void 이름이_비어_있으면_400을_응답하고_저장하지_않는다() {
        Map<String, String> params = Map.of("name", "", "date", "2023-08-05", "time", "15:40");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);

        assertReservationCount(0);
    }

    @Test
    void 존재하지_않는_날짜와_시간이면_400을_응답하고_저장하지_않는다() {
        Map<String, String> params = Map.of("name", "브라운", "date", "2023-13-45", "time", "25:99");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);

        assertReservationCount(0);
    }

    @Test
    void 없는_예약을_삭제하면_404를_응답한다() {
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(404);
    }

    private void assertReservationCount(int count) {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(count));
    }
}
