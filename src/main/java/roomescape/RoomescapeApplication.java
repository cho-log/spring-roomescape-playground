package roomescape;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoomescapeApplication {
    @Bean
    public Clock reservationClock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(RoomescapeApplication.class, args);
    }

}
