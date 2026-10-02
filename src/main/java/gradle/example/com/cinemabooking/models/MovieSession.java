package gradle.example.com.cinemabooking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("session")
public class MovieSession {

    @Id
    private Long id;
    @Transient
    private Movie movie;
    @Transient
    private Hall hall;
    @Column("movie_id")
    private Long movieId;
    @Column("hall_id")
    private Long hallId;
    @Column("start_time")
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
        this.movieId = movie == null ? null : movie.getId();
        this.hallId = hall == null ? null : hall.getId();
        this.startTime = startTime;
        this.price = price;
    }

    public MovieSession(Long id, Long movieId, Long hallId, LocalDateTime startTime, Integer price) {
        this.id = id;
        this.movieId = movieId;
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

    public Hall getHall() {
        return hall;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Integer getPrice() {
        return price;
    }

    public Long getMovieId() {
        return movieId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        this.movieId = movie == null ? null : movie.getId();
    }
    public void setHall(Hall hall) {
        this.hall = hall;
        this.hallId = hall == null ? null : hall.getId();
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
