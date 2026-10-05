package gradle.example.com.cinemabooking.exceptions;

public class BookingAccessDeniedException extends RuntimeException {
    public BookingAccessDeniedException() {
        super("You don't have permission to access this resource");
    }
}
