package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.MovieSession;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface MovieSessionRepository extends
        ListCrudRepository<MovieSession, Long>,
        MovieSessionRepositoryCustom {
    String SESSION_SELECT = """
        SELECT s.id AS session_id, s.start_time, s.price,
               h.id AS hall_id, h.number AS hall_number,
               m.id AS movie_id, m.title AS movie_title,
               m.duration AS movie_duration, m.age_rating AS movie_age_rating,
               m.description AS movie_description
        FROM session s
        JOIN movie m ON m.id = s.movie_id
        JOIN hall h ON h.id = s.hall_id
        """;

    @Query("""
        SELECT COUNT(s.id)
        FROM session s
        JOIN movie m ON s.movie_id = m.id
        WHERE s.hall_id = :hallId
          AND :startTime < (s.start_time + m.duration * INTERVAL '1 minute')
          AND :endTime > s.start_time
        """)
    Integer countOverlaps(Long hallId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
        SELECT COUNT(s.id)
        FROM session s
        JOIN movie m ON s.movie_id = m.id
        WHERE s.hall_id = :hallId
          AND s.id <> :sessionId
          AND :startTime < (s.start_time + m.duration * INTERVAL '1 minute')
          AND :endTime > s.start_time
        """)
    Integer countOverlapsExcludingSession(
            Long hallId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long sessionId
    );

    default Long save(Long movieId, Long hallId, LocalDateTime startTime, Integer price) {
        return save(new MovieSession(null, movieId, hallId, startTime, price)).getId();
    }

    default MovieSession patch(MovieSession request) {
        return save(request);
    }

    @Query("SELECT COUNT(id) FROM booking WHERE session_id = :id")
    int countBookings(Long id);

    @Override
    @Query(value = SESSION_SELECT + " WHERE s.id = :id", rowMapperRef = "movieSessionMapper")
    Optional<MovieSession> findById(Long id);

    @Query("""
        SELECT COUNT(*)
        FROM session s
        JOIN movie m ON m.id = s.movie_id
        WHERE (CAST(:movieTitle AS varchar) IS NULL OR CAST(:movieTitle AS varchar) = ''
               OR LOWER(m.title) LIKE LOWER(CONCAT('%', CAST(:movieTitle AS varchar), '%')))
          AND (CAST(:date AS date) IS NULL OR (s.start_time >= CAST(:date AS date)
               AND s.start_time < CAST(:date AS date) + INTERVAL '1 day'))
        """)
    long count(String movieTitle, LocalDate date);
}
