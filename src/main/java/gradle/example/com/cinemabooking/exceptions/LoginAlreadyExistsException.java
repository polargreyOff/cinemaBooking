package gradle.example.com.cinemabooking.exceptions;

public class LoginAlreadyExistsException extends RuntimeException {
    public LoginAlreadyExistsException() {
        super("Login already exists");
    }
}
