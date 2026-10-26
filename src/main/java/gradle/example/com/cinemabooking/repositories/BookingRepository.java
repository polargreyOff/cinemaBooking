package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.Booking;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface BookingRepository extends ListCrudRepository<Booking, Long> {
    List<Booking> findAllByUserIdOrderByBookingTimeDesc(Long userId);

    List<Booking> findAllBySessionId(Long sessionId);

    boolean existsBySessionIdAndSeatId(Long sessionId, Long seatId);

    long countBySessionId(Long sessionId);

    default Long save(Long sessionId, Long seatId, Long userId) {
        return save(new Booking(null, userId, sessionId, seatId, null)).getId();
    }
}
