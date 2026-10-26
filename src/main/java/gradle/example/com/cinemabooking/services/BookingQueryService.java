package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.models.Booking;
import gradle.example.com.cinemabooking.repositories.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingQueryService {
    private final BookingRepository bookingRepository;

    public BookingQueryService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasBookingsForSession(Long sessionId) {
        return bookingRepository.countBySessionId(sessionId) > 0;
    }

    @Transactional(readOnly = true)
    public Set<Long> bookedSeatIds(Long sessionId) {
        return bookingRepository.findAllBySessionId(sessionId).stream()
                .map(Booking::getSeatId)
                .collect(Collectors.toSet());
    }
}
