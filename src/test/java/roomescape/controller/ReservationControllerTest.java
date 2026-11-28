package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReservationControllerTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void 이름이_비어있으면_400을_반환한다(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("date", LocalDate.now().plusDays(1).toString());
        params.put("time", "15:40");

        RestAssured.given()
                   .contentType(ContentType.JSON)
                   .body(params)
                   .when().post("/reservations")
                   .then().statusCode(400);
    }

    @Test
    void 이름이_255자를_넘으면_400을_반환한다() {
        String name = "a".repeat(256);
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("date", LocalDate.now().plusDays(1).toString());
        params.put("time", "15:40");

        RestAssured.given()
                   .contentType(ContentType.JSON)
                   .body(params)
                   .when().post("/reservations")
                   .then().statusCode(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":null,\"date\":\"2099-11-22\",\"time\":\"15:30\"}",
            "{\"name\":\"홍길동\",\"date\":null,\"time\":\"15:30\"}",
            "{\"name\":\"홍길동\",\"date\":\"2099-11-22\",\"time\":null}"
    })
    void 필수값이_null이면_400을_반환한다(String request) {
        RestAssured.given()
                   .contentType(ContentType.JSON)
                   .body(request)
                   .when().post("/reservations")
                   .then().statusCode(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"홍길동\",\"date\":\"2030-02-30\",\"time\":\"15:30\"}",
            "{\"name\":\"홍길동\",\"date\":\"2030-11-22\",\"time\":\"25:00\"}"
    })
    void 날짜나_시간이_유효하지_않으면_400을_반환한다(String request) {
        RestAssured.given()
                   .contentType(ContentType.JSON)
                   .body(request)
                   .when().post("/reservations")
                   .then().statusCode(400);
    }
}
