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
    @ExceptionHandler(SeatsAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleSeatsAlreadyExist(SeatsAlreadyExistException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BookingCancellationUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleBookingCancellationUnavailable(BookingCancellationUnavailableException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BookingAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleBookingAccessDeniedException(BookingAccessDeniedException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.FORBIDDEN, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyBookedException(SeatAlreadyBookedException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(SeatDoesNotBelongToHallException.class)
    public ResponseEntity<ErrorResponse> handleSeatDoesNotBelongToHallException(SeatDoesNotBelongToHallException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(SessionAlreadyStartedException.class)
    public ResponseEntity<ErrorResponse> handleSessionAlreadyStarted(SessionAlreadyStartedException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(SessionHasBookingsException.class)
    public ResponseEntity<ErrorResponse> handleSessionHasBookingsException(SessionHasBookingsException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }


    @ExceptionHandler(InvalidSessionTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSessionTime(
            InvalidSessionTimeException exception
    ) {

        ErrorResponse error = ErrorResponse.create(exception, HttpStatus.BAD_REQUEST, exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(SessionTimeConflictException.class)
    public ResponseEntity<ErrorResponse> handleSessionTimeConflictException(SessionTimeConflictException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.CONFLICT, ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MovieSessionNotFound.class)
    public ResponseEntity<ErrorResponse> handleMovieSessionNotFound(MovieSessionNotFound e) {
        ErrorResponse response = ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

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
