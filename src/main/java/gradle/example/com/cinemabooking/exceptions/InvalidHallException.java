package gradle.example.com.cinemabooking.exceptions;

public class InvalidHallException extends RuntimeException {
    public InvalidHallException(String message) {
        super(message);
    }
}
