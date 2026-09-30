package gradle.example.com.cinemabooking.exceptions;

public class SessionAlreadyStartedException extends RuntimeException {
    public SessionAlreadyStartedException() {
        super("Session already started");
    }
}
