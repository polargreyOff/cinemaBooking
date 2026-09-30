package gradle.example.com.cinemabooking.exceptions;

public class BookingCancellationUnavailableException extends RuntimeException {
    public BookingCancellationUnavailableException() {
        super("Booking cannot be cancelled after session start");
    }
}
