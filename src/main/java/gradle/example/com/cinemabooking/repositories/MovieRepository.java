package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.dto.UpdateMovieRequest;
import gradle.example.com.cinemabooking.generated.api.MoviesApi;
import gradle.example.com.cinemabooking.models.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MovieRepository {
    private final JdbcClient jdbcClient;

    @Autowired
    public MovieRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Movie> findAll() {
        return jdbcClient.sql("""
        SELECT *
        FROM movie;   
        """)
        .query((rs, rowNum) -> {
            return new Movie(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getInt("duration"),
                rs.getString("age_rating"),
                rs.getString("description")
            );
        }).list();
    }

    public Optional<Movie> findById(Long id) {
        return jdbcClient.sql("""
        SELECT * FROM movie WHERE id = :id
        """)
        .param("id", id)
        .query(Movie.class).optional();
    }

    public Movie save(Movie movie) {
        return jdbcClient.sql("""
        INSERT INTO movie(title, duration, age_rating, description) 
        VALUES (:title, :duration, :age_rating, :description)
        RETURNING id, title, duration, age_rating, description;
        """).param("title", movie.getTitle())
        .param("duration", movie.getDuration())
        .param("age_rating", movie.getAge_rating())
        .param("description", movie.getDescription())
        .query(Movie.class).single();
    }

    public int deleteById(Long id) {
        return jdbcClient.sql("""
        DELETE FROM movie
        WHERE id = :id
        """)
        .param("id", id)
        .update();
    }

    public Movie update(Movie movie) {
        return jdbcClient.sql("""
        UPDATE movie SET title=:title, duration=:duration, age_rating=:age_rating, description=:description
        WHERE id = :id
        RETURNING id, title, duration, age_rating, description
        """)
                .param("id", movie.getId())
                .param("title", movie.getTitle())
                .param("age_rating", movie.getAge_rating())
                .param("description", movie.getDescription())
                .param("duration", movie.getDuration())
                .query(Movie.class).single();
    }
 }
