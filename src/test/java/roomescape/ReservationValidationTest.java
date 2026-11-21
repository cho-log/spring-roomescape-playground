package roomescape;

import static org.hamcrest.CoreMatchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationValidationTest {

    @Test
    void 올바른_값이면_예약이_생성된다() {
        postReservation("브라운", "2023-08-05", "15:40")
                .statusCode(201);
    }

    @Test
    void 이름에_숫자가_있으면_예외가_발생한다() {
        postReservation("브라운1", "2023-08-05", "15:40")
                .statusCode(400)
                .body("message", is("이름에 숫자를 포함할 수 없습니다."));
    }

    @Test
    void 날짜_형식이_틀리면_예외가_발생한다() {
        postReservation("브라운", "2023/08/05", "15:40")
                .statusCode(400)
                .body("message", is("날짜는 yyyy-MM-dd 형식이어야 합니다."));
    }

    @Test
    void 시간_형식이_틀리면_예외가_발생한다() {
        postReservation("브라운", "2023-08-05", "15시40분")
                .statusCode(400)
                .body("message", is("시간은 HH:mm 형식이어야 합니다."));
    }

    @Test
    void 이름이_비어있으면_입력_요청_메시지가_나온다() {
        postReservation("", "2023-08-05", "15:40")
                .statusCode(400)
                .body("message", is("이름을 입력해야 합니다."));
    }

    @Test
    void 날짜가_비어있으면_입력_요청_메시지가_나온다() {
        postReservation("브라운", "", "15:40")
                .statusCode(400)
                .body("message", is("날짜를 입력해야 합니다."));
    }

    @Test
    void 시간이_비어있으면_입력_요청_메시지가_나온다() {
        postReservation("브라운", "2023-08-05", "")
                .statusCode(400)
                .body("message", is("시간을 입력해야 합니다."));
    }

    @Test
    void 같은_날짜와_시간에_예약하면_예외가_발생한다() {
        postReservation("브라운", "2026-11-21", "10:00")
                .statusCode(201);

        postReservation("핑크", "2026-11-21", "10:00")
                .statusCode(409)
                .body("message", is("이미 예약된 시간입니다."));
    }

    @Test
    void 날짜가_같아도_시간이_다르면_예약할_수_있다() {
        postReservation("브라운", "2026-11-21", "10:00")
                .statusCode(201);

        postReservation("핑크", "2026-11-21", "11:00")
                .statusCode(201);
    }

    @Test
    void 시간이_같아도_날짜가_다르면_예약할_수_있다() {
        postReservation("브라운", "2026-11-21", "10:00")
                .statusCode(201);

        postReservation("핑크", "2026-11-22", "10:00")
                .statusCode(201);
    }

    @Test
    void 이름이_10자_초과면_예외가_발생한다() {
        postReservation("이름열자넘으면안됩니다", "2026-11-21", "10:00")
                .statusCode(400)
                .body("message", is("이름은 10자 이내여야 합니다."));
    }

    @Test
    void 이름이_10자_이내면_정상적으로_예약된다() {
        postReservation("이름열자넘으면안돼요", "2026-11-21", "10:00")
                .statusCode(201);
    }

    private ValidatableResponse postReservation(String name, String date, String time) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("date", date);
        params.put("time", time);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all();
    }
}
