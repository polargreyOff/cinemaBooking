package gradle.example.com.cinemabooking.exceptions;

public class MovieSessionNotFound extends RuntimeException {
    public MovieSessionNotFound(Long id) {
        super("Session with id " + id + " not found");
    }
}
