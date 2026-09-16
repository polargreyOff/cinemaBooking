package gradle.example.com.cinemabooking.exceptions;

public class InvalidMovieException extends RuntimeException{
    public InvalidMovieException(String message){
        super(message);
    }
}
