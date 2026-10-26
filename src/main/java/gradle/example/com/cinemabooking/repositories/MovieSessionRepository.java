package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.MovieSession;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieSessionRepository extends ListCrudRepository<MovieSession, Long> {
    List<MovieSession> findByHallId(Long hallId);

    default Long save(Long movieId, Long hallId, LocalDateTime startTime, Integer price) {
        return save(new MovieSession(null, movieId, hallId, startTime, price)).getId();
    }

    default MovieSession patch(MovieSession request) {
        return save(request);
    }
}
