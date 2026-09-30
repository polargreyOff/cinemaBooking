package gradle.example.com.cinemabooking.exceptions;

public class BookingAccessDeniedException extends RuntimeException {
    public BookingAccessDeniedException() {
        super("You cannot delete another user's booking");
    }
}
