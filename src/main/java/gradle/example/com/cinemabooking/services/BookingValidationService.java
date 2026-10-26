package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.BookingAccessDeniedException;
import gradle.example.com.cinemabooking.exceptions.BookingCancellationUnavailableException;
import gradle.example.com.cinemabooking.exceptions.SeatAlreadyBookedException;
import gradle.example.com.cinemabooking.exceptions.SeatDoesNotBelongToHallException;
import gradle.example.com.cinemabooking.exceptions.SessionAlreadyStartedException;
import gradle.example.com.cinemabooking.generated.model.SeatResponse;
import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.generated.model.UserResponse;
import gradle.example.com.cinemabooking.models.Booking;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingValidationService {
    public void validateDeleteBooking(UserResponse user, Booking booking, SessionResponse session) {
        if (!booking.getUserId().equals(user.getId())) {
            throw new BookingAccessDeniedException();
        }
        if (!LocalDateTime.now().isBefore(session.getStartTime())) {
            throw new BookingCancellationUnavailableException();
        }
    }

    public void validateCreateBooking(SessionResponse session, SeatResponse seat, boolean alreadyBooked) {
        if (!seat.getHallId().equals(session.getHall().getId())) {
            throw new SeatDoesNotBelongToHallException();
        }
        if (!LocalDateTime.now().isBefore(session.getStartTime())) {
            throw new SessionAlreadyStartedException();
        }
        if (alreadyBooked) {
            throw new SeatAlreadyBookedException();
        }
    }
}
