package gradle.example.com.cinemabooking.models;

import java.time.LocalDateTime;

public class MovieSession {

    private Long id;
    private Movie movie;
    private Long hallId;
    private LocalDateTime startTime;
    private Integer price;

    public MovieSession(
            Long id,
            Movie movie,
            Long hallId,
            LocalDateTime startTime,
            Integer price
    ) {
        this.id = id;
        this.movie = movie;
        this.hallId = hallId;
        this.startTime = startTime;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Long getHallId() {
        return hallId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Integer getPrice() {
        return price;
    }
}