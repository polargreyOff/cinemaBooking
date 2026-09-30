package gradle.example.com.cinemabooking.models;

import java.time.LocalDateTime;

public class Booking {

    private Long id;
    private Long userId;
    private MovieSession session;
    private Seat seat;
    private LocalDateTime bookingTime;

    public Booking(
            Long id,
            Long userId,
            MovieSession session,
            Seat seat,
            LocalDateTime bookingTime
    ) {
        this.id = id;
        this.userId = userId;
        this.session = session;
        this.seat = seat;
        this.bookingTime = bookingTime;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public MovieSession getSession() {
        return session;
    }

    public Seat getSeat() {
        return seat;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
}