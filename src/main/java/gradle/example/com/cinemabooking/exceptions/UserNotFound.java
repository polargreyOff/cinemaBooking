package gradle.example.com.cinemabooking.exceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String username) {
        super("User with login " + username + " not found");
    }
}
