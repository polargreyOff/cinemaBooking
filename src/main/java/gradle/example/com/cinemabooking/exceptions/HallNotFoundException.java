package gradle.example.com.cinemabooking.exceptions;

public class HallNotFoundException extends RuntimeException {
    public HallNotFoundException(Long id) {
        super("Hall with id " + id + " not found");
    }
}
