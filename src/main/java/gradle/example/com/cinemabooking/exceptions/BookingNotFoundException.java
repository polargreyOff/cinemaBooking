package gradle.example.com.cinemabooking.exceptions;

public class BookingNotFoundException extends RuntimeException{
    public BookingNotFoundException(Long id) {
        super("Booking with id " + id + " not found");
    }
}
