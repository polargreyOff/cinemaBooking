package gradle.example.com.cinemabooking.exceptions;

public class SeatDoesNotBelongToHallException extends RuntimeException {
    public SeatDoesNotBelongToHallException() {
        super("Seat does not belong to the session hall");
    }
}
