package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SeatRepository {
    private JdbcClient jdbcClient;

    @Autowired
    public SeatRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Seat> findByHallId(Long hallId) {
        return jdbcClient.sql("""
            SELECT * FROM seat WHERE hall_id = :hallId
            ORDER BY row_number, seat_number
        """)
        .param("hallId", hallId)
        .query(Seat.class)
        .list();
    }

    public Optional<Seat> findById(Long seatId) {
        return jdbcClient.sql("""
            SELECT * FROM seat WHERE id = :seatId
        """)
        .param("seatId", seatId)
        .query(Seat.class)
        .optional();
    }

    public Seat save(Long hallId, Seat seat) {
        return jdbcClient.sql("""
            INSERT INTO SEAT(hall_id, row_number, seat_number)
            VALUES (:hallId, :rowNumber, :seatNumber)
            RETURNING id, hall_id, row_number, seat_number
        """)
        .param("hallId", hallId)
        .param("rowNumber", seat.getRowNumber())
        .param("seatNumber", seat.getSeatNumber())
        .query(Seat.class)
        .single();
    }

    public int deleteById (Long seatId) {
        return jdbcClient.sql("""
        DELETE FROM seat WHERE id = :id
        """)
        .param("id", seatId)
        .update();
    }
}
