package gradle.example.com.cinemabooking.exceptions;

public class SessionHasBookingsException extends RuntimeException {

    public SessionHasBookingsException(Long sessionId) {
        super("Session with id " + sessionId + " has bookings and cannot be deleted");
    }
}