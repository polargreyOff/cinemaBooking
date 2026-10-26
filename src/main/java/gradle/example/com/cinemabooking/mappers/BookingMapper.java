package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.BookingResponse;
import gradle.example.com.cinemabooking.generated.model.SeatResponse;
import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.models.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingResponse toBookingResponse(Booking booking, SessionResponse session, SeatResponse seat) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setSession(session);
        response.setSeat(seat);
        response.setBookingTime(booking.getBookingTime());
        return response;
    }
}
