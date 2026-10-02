package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.Booking;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends ListCrudRepository<Booking, Long> {
    String BOOKING_SELECT = """
        SELECT b.user_id, b.id AS booking_id, b.booking_time,
               se.id AS seat_id, se.row_number, se.seat_number,
               s.id AS session_id, s.start_time, s.price,
               h.id AS hall_id, h.number AS hall_number,
               m.id AS movie_id, m.title AS movie_title,
               m.duration AS movie_duration, m.age_rating AS movie_age_rating,
               m.description AS movie_description
        FROM booking b
        JOIN seat se ON b.seat_id = se.id
        JOIN session s ON b.session_id = s.id
        JOIN movie m ON s.movie_id = m.id
        JOIN hall h ON s.hall_id = h.id
        """;

    default Long save(Long sessionId, Long seatId, Long userId) {
        Booking booking = new Booking(null, userId, sessionId, seatId, null);
        return save(booking).getId();
    }

    boolean existsBySessionIdAndSeatId(Long sessionId, Long seatId);

    default boolean existsActiveBooking(Long sessionId, Long seatId) {
        return existsBySessionIdAndSeatId(sessionId, seatId);
    }

    @Override
    @Query(value = BOOKING_SELECT + " WHERE b.id = :bookingId", rowMapperRef = "bookingMapper")
    Optional<Booking> findById(Long bookingId);

    @Query(value = BOOKING_SELECT + " WHERE b.user_id = :userId ORDER BY b.booking_time DESC",
            rowMapperRef = "bookingMapper")
    List<Booking> findAllByUserId(Long userId);
}
