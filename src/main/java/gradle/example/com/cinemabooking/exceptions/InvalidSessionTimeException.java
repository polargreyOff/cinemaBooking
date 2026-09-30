package gradle.example.com.cinemabooking.exceptions;

public class InvalidSessionTimeException
        extends RuntimeException {

    public InvalidSessionTimeException() {
        super("Session cannot start in the past");
    }
}
