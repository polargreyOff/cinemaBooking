package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.BookingResponse;
import gradle.example.com.cinemabooking.models.Booking;
import gradle.example.com.cinemabooking.models.MovieSession;
import gradle.example.com.cinemabooking.models.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BookingMapper {
    private MovieSessionMapper movieSessionMapper;
    private SeatMapper seatMapper;

    @Autowired
    public BookingMapper(
            MovieSessionMapper movieSessionMapper,
            SeatMapper seatMapper
    ) {
        this.movieSessionMapper = movieSessionMapper;
        this.seatMapper = seatMapper;
    }

    public Booking resultSetToBooking(ResultSet rs, int rowNum) throws SQLException {
        MovieSession movieSession = movieSessionMapper.ResultSetToMovieSession(rs, rowNum);

        Seat seat = new Seat(
            rs.getLong("seat_id"),
            rs.getLong("hall_id"),
            rs.getInt("row_number"),
            rs.getInt("seat_number")
        );

        return new Booking(
            rs.getLong("booking_id"),
            rs.getLong("user_id"),
            movieSession,
            seat,
            rs.getTimestamp("booking_time").toLocalDateTime()
            );
    }

    public BookingResponse toBookingResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getId());
        bookingResponse.setSession(
                movieSessionMapper.toSessionResponse(booking.getSession())
        );
        bookingResponse.setSeat(
                seatMapper.toSeatResponse(booking.getSeat())
        );
        bookingResponse.setBookingTime(booking.getBookingTime());

        return bookingResponse;
    }
}
