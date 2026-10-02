package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.Seat;
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

    @Modifying
    @Query("DELETE FROM seat WHERE id = :id")
    int deleteExistingById(Long id);
}
