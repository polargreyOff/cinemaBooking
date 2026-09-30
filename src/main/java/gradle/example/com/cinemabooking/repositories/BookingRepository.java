package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.mappers.BookingMapper;
import gradle.example.com.cinemabooking.models.Booking;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {
    private final JdbcClient jdbcClient;
    private final BookingMapper bookingMapper;

    public BookingRepository(
            JdbcClient jdbcClient,
            BookingMapper bookingMapper
    ) {
        this.jdbcClient = jdbcClient;
        this.bookingMapper = bookingMapper;
    }

    public Long save(
            Long sessionId,
            Long seatId,
            Long userId
    ) {
        return jdbcClient.sql("""
            INSERT INTO booking(user_id, session_id, seat_id)
            VALUES (:userId, :sessionId, :seatId)
            RETURNING id
        """)
        .param("userId", userId)
        .param("sessionId", sessionId)
        .param("seatId", seatId)
        .query(Long.class)
        .single();
    }

    public boolean existsActiveBooking(Long sessionId, Long seatId) {
        return jdbcClient.sql("""
            SELECT EXISTS (
                            SELECT 1
                            FROM booking
                            WHERE session_id = :sessionId
                              AND seat_id = :seatId
                        )
        """)
        .param("sessionId", sessionId)
        .param("seatId", seatId)
        .query(Boolean.class)
        .single();
    }

    public Optional<Booking> findById(Long bookingId) {
        return jdbcClient.sql("""
            SELECT 
                user_id,
                
                b.id AS booking_id,
                b.booking_time,
                
                se.id AS seat_id,
                se.row_number,
                se.seat_number,
            
                s.id AS session_id,
                s.start_time,
                s.price,
           
                h.id AS hall_id,
                h.number as hall_number,
    
                m.id AS movie_id,
                m.title AS movie_title,
                m.duration AS movie_duration,
                m.age_rating AS movie_age_rating,
                m.description AS movie_description
            FROM booking b
                JOIN seat se on b.seat_id = se.id
                JOIN session s ON b.session_id = s.id
                    JOIN movie m ON s.movie_id = m.id
                    JOIN hall h ON s.hall_id = h.id
            WHERE b.id = :bookingId
                
        """)
                .param("bookingId", bookingId)
                .query(bookingMapper::resultSetToBooking)
                .optional();
    }

    public List<Booking> findAllByUserId(Long userId) {
        return jdbcClient.sql("""
            SELECT 
                user_id,
                
                b.id AS booking_id,
                b.booking_time,
                
                se.id AS seat_id,
                se.row_number,
                se.seat_number,
            
                s.id AS session_id,
                s.start_time,
                s.price,
           
                h.id AS hall_id,
                h.number as hall_number,
    
                m.id AS movie_id,
                m.title AS movie_title,
                m.duration AS movie_duration,
                m.age_rating AS movie_age_rating,
                m.description AS movie_description
            FROM booking b
                JOIN seat se on b.seat_id = se.id
                JOIN session s ON b.session_id = s.id
                    JOIN movie m ON s.movie_id = m.id
                    JOIN hall h ON s.hall_id = h.id
            WHERE user_id = :userId
                
        """)
        .param("userId", userId)
        .query(bookingMapper::resultSetToBooking)
        .list();
    }
}
