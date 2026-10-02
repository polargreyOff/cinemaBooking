package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.Movie;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface MovieRepository extends ListCrudRepository<Movie, Long> {
    default Movie update(Movie movie) {
        return save(movie);
    }

    @Modifying
    @Query("DELETE FROM movie WHERE id = :id")
    int deleteExistingById(Long id);
}
