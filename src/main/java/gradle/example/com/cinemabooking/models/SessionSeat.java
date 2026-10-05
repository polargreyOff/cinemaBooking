package gradle.example.com.cinemabooking.models;

public record SessionSeat (
        Long id,
        Integer rowNumber,
        Integer seatNumber,
        boolean available
) {

}