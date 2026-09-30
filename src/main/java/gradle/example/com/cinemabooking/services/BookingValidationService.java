package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.SeatAlreadyBookedException;
import gradle.example.com.cinemabooking.exceptions.SeatDoesNotBelongToHallException;
import gradle.example.com.cinemabooking.exceptions.SessionAlreadyStartedException;
import gradle.example.com.cinemabooking.generated.model.CreateBookingRequest;
import gradle.example.com.cinemabooking.generated.model.SeatResponse;
import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.models.MovieSession;
import gradle.example.com.cinemabooking.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingValidationService {
    private final BookingRepository bookingRepository;

    public BookingValidationService(
            BookingRepository bookingRepository
    ) {
        this.bookingRepository = bookingRepository;
    }

    public void validateCreateBooking(SessionResponse session, SeatResponse seat) {
        if (!seat.getHallId().equals(session.getHall().getId()) ) {
            throw new SeatDoesNotBelongToHallException();
        }

        if (LocalDateTime.now().isAfter(session.getStartTime())) {
            throw new SessionAlreadyStartedException();
        }

        if (bookingRepository.existsActiveBooking(session.getId(), seat.getId())) {
            throw new SeatAlreadyBookedException();
        }
    }
}
