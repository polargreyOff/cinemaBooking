package gradle.example.com.cinemabooking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        ErrorResponse response = ErrorResponse.create(e, HttpStatus.UNAUTHORIZED, "Invalid login or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getBindingResult().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMovieNotFound(MovieNotFoundException exception) {

        ErrorResponse response = ErrorResponse.create(exception, HttpStatus.NOT_FOUND, exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidMovieException.class)
    public ResponseEntity<ErrorResponse> handleMovieBadRequest(InvalidMovieException exception) {
        ErrorResponse response = ErrorResponse.create(exception, HttpStatus.BAD_REQUEST, exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidHallException.class)
    public  ResponseEntity<ErrorResponse> handleInvalidHall(InvalidHallException exception) {
        ErrorResponse response = ErrorResponse.create(exception, HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HallNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHallNotFound(HallNotFoundException exception) {
        ErrorResponse response = ErrorResponse.create(exception, HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotFound(SeatNotFoundException exception) {
        ErrorResponse response = ErrorResponse.create(exception, HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidSeatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSeat(InvalidSeatException exception) {
        ErrorResponse response = ErrorResponse.create(exception, HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(LoginAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLoginAlreadyExists(
            LoginAlreadyExistsException exception
    ) {
        ErrorResponse response = ErrorResponse.create(exception, HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
