package roomescape.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import roomescape.model.ReservationDTO;

import javax.sql.DataSource;
import java.util.List;

public class JdbcReservationRepository implements ReservationRepository {

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public ReservationDTO reservationAdd(ReservationDTO reservationDTO) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(reservationDTO);
        Number key = jdbcInsert.executeAndReturnKey(param);
        reservationDTO.setId(key.intValue());
        return reservationDTO;
    }

    @Override
    public List<ReservationDTO> findAll() {
        String sql = "select * from reservation";
        return jdbcTemplate.query(sql, reservationRowMapper());
    }

    private RowMapper<ReservationDTO> reservationRowMapper() {
        return BeanPropertyRowMapper.newInstance(ReservationDTO.class);
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("delete from reservation where id = ?", id);
    }
}
