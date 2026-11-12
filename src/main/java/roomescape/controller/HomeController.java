package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller //NestJS의 @Controller와 비슷한거임. 이 클래스는 컨트롤러야 라고 알려주는거임
public class HomeController {

    @GetMapping("/") //nest의 @Get()과 비슷한거임. /로 들어오면 home()을 실행시켜라
    public String home() { //얘는 그냥 메소드이름임
        return "home"; //home.html을 찾아서 렌더링해라. thymeleaf가 알아서 해줌.
    }

}
