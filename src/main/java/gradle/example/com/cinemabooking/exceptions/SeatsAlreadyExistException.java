package gradle.example.com.cinemabooking.exceptions;

public class SeatsAlreadyExistException extends RuntimeException {
    public SeatsAlreadyExistException() {
        super("Seats already exists");
    }
}
