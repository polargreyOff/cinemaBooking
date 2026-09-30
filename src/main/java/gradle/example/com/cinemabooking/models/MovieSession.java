package gradle.example.com.cinemabooking.models;

import java.time.LocalDateTime;

public class MovieSession {

    private Long id;
    private Movie movie;
    private Hall hall;
    private LocalDateTime startTime;
    private Integer price;

    public MovieSession() {}
    public MovieSession(
            Long id,
            Movie movie,
            Hall hall,
            LocalDateTime startTime,
            Integer price
    ) {
        this.id = id;
        this.movie = movie;
        this.hall = hall;
        this.startTime = startTime;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Hall getHall() {
        return hall;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Integer getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}