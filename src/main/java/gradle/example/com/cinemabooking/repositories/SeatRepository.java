package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.generated.model.SessionSeatResponse;
import gradle.example.com.cinemabooking.models.Seat;
import gradle.example.com.cinemabooking.models.SessionSeat;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface SeatRepository extends ListCrudRepository<Seat, Long> {
    @Modifying
    @Query("""
        INSERT INTO seat (hall_id, row_number, seat_number)
        SELECT :hallId, row_number, seat_number
        FROM generate_series(1, :rows) AS row_number
        CROSS JOIN generate_series(1, :seatsPerRow) AS seat_number
        """)
    int generateSeats(Long hallId, int rows, int seatsPerRow);



    List<Seat> findByHallIdOrderByRowNumberAscSeatNumberAsc(Long hallId);

    default List<Seat> findByHallId(Long hallId) {
        return findByHallIdOrderByRowNumberAscSeatNumberAsc(hallId);
    }

    default Seat save(Long hallId, Seat seat) {
        seat.setHallId(hallId);
        return save(seat);
    }

    @Query("""
        SELECT
            seat.id as id,
            seat.row_number as row_number,
            seat.seat_number as seat_number,
            CASE
                WHEN b.id IS NULL THEN TRUE
                ELSE FALSE
            END as available
        FROM session s JOIN seat
            ON s.hall_id = seat.hall_id
        LEFT JOIN booking b
            ON b.seat_id = seat.id
            AND b.session_id = s.id
        WHERE s.id = :sessionId
        ORDER BY seat.row_number, seat.seat_number
    """)
    List<SessionSeat> findSeatsBySessionId(Long sessionId);

    @Modifying
    @Query("DELETE FROM seat WHERE id = :id")
    int deleteExistingById(Long id);
}
