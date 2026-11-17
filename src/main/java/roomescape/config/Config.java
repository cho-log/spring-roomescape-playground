package roomescape.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.repository.JdbcReservationRepository;
import roomescape.repository.ReservationRepository;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final DataSource dataSource;

    @Bean
    public ReservationRepository reservationRepository(){
        return new JdbcReservationRepository(dataSource);
    }
}
