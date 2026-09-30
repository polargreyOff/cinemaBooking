package gradle.example.com.cinemabooking.exceptions;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException() {
        super("Seat already booked for this session");
    }
}
