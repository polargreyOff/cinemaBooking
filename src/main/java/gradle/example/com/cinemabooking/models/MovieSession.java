package gradle.example.com.cinemabooking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("session")
public class MovieSession {

    @Id
    private Long id;
    @Column("movie_id")
    private Long movieId;
    @Column("hall_id")
    private Long hallId;
    @Column("start_time")
    private LocalDateTime startTime;
    private Integer price;

    public MovieSession() {}
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
