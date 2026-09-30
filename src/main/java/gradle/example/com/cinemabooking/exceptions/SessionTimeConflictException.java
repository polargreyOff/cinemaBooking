package gradle.example.com.cinemabooking.exceptions;

public class SessionTimeConflictException extends RuntimeException {
    public SessionTimeConflictException(Long id) {
        super("Hall with id " + id + " occupied at this time");
    }
}
