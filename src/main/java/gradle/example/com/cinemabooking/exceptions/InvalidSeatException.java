package gradle.example.com.cinemabooking.exceptions;

public class InvalidSeatException extends RuntimeException {
    public InvalidSeatException(String message) {
        super(message);
    }
}
