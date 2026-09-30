package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.mappers.MovieSessionMapper;
import gradle.example.com.cinemabooking.models.MovieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MovieSessionRepository {
    private JdbcClient jdbcClient;
    private MovieSessionMapper movieSessionMapper;

    @Autowired
    public MovieSessionRepository(JdbcClient jdbcClient, MovieSessionMapper movieSessionMapper) {
        this.jdbcClient = jdbcClient;
        this.movieSessionMapper = movieSessionMapper;
    }



    public Integer countOverlaps(Long hallId, LocalDateTime startTime, LocalDateTime endTime) {
        return jdbcClient.sql("""
            SELECT COUNT(s.id) FROM session s 
            JOIN movie m ON s.movie_id = m.id 
            WHERE s.hall_id = :hallId
            AND :startTime < (s.start_time + m.duration * INTERVAL '1 minute')
            AND :endTime > s.start_time
        """)
        .param("hallId", hallId)
        .param("startTime", startTime)
        .param("endTime", endTime)
        .query(Integer.class)
        .single();
    }

    public Long save(
            Long movieId,
            Long hallId,
            LocalDateTime startTime,
            Integer price
    ) {
        return jdbcClient.sql("""
            INSERT INTO session (movie_id, hall_id, start_time, price)
            VALUES (:movieId, :hallId, :startTime, :price)
            RETURNING id
        """)
        .param("movieId", movieId)
        .param("hallId", hallId)
        .param("startTime", startTime)
        .param("price", price)
        .query(Long.class)
        .single();
    }

    public int deleteById(Long id) {
        return jdbcClient.sql("""
            DELETE FROM session WHERE id = :id
        """)
        .param("id", id)
        .update();
    }

    public int countBookings(Long id) {
        return jdbcClient.sql("""
            SELECT COUNT(id) FROM booking WHERE session_id = :id;
            
        """)
        .param("id", id)
        .query(Integer.class)
        .single();
    }

    public Optional<MovieSession> findById(Long id) {
        return jdbcClient.sql("""
            SELECT 
                s.id AS session_id,
                s.start_time,
                s.price,
                h.id AS hall_id,
                h.number as  hall_number,
                m.id as movie_id,
                m.title as movie_title,
                m.duration as movie_duration,
                m.age_rating as movie_age_rating,
                m.description as movie_description
            from session s
            JOIN movie m ON s.movie_id = m.id
            JOIN hall h ON s.hall_id = h.id
            WHERE s.id = :id
           
        """)
        .param("id", id)
        .query(movieSessionMapper::ResultSetToMovieSession)
        .optional();
    }

    public List<MovieSession> findAll(
            String movieTitle,
            LocalDate date,
            int page,
            int size,
            String sort,
            String direction
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            s.id AS session_id,
            s.start_time,
            s.price,
       
            h.id AS hall_id,
            h.number as hall_number,

            m.id AS movie_id,
            m.title AS movie_title,
            m.duration AS movie_duration,
            m.age_rating AS movie_age_rating,
            m.description AS movie_description

        FROM session s

        JOIN movie m
            ON m.id = s.movie_id
        JOIN hall h
            ON h.id = s.hall_id

        WHERE 1 = 1
        """);

        if (movieTitle != null &&  !movieTitle.isEmpty()) {
            sql.append("""
                AND LOWER(m.title) LIKE LOWER(:movie_title)
            """);
        }
        if (date != null) {
            sql.append("""
                AND s.start_time >= :startTime
                AND s.start_time < :endTime
            """);
        }

        String orderBy;
        switch (sort) {
            case "price":
                orderBy = "s.price";
                break;
            case "movieTitle":
                orderBy = "m.title";
                break;
            default:
                orderBy = "s.start_time";
        }

        String orderDirection = "desc".equalsIgnoreCase(direction) ? "DESC" : "ASC";

        sql.append("ORDER BY " + orderBy + " " + orderDirection
        + " LIMIT :size OFFSET :offset");

        JdbcClient.StatementSpec query = jdbcClient.sql(sql.toString());
        query.param("size", size);
        query.param("offset", page*size);

        if (movieTitle != null &&  !movieTitle.isEmpty()) {
            query.param("movie_title", "%" + movieTitle + "%");
        }

        if (date != null) {
            query.param("startTime", date.atStartOfDay());
            query.param("endTime", date.plusDays(1).atStartOfDay());
        }

        return query.query(movieSessionMapper::ResultSetToMovieSession).list();
    }

    public long count(String movieTitle, LocalDate date) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM session s
            JOIN movie m
            ON m.id = s.movie_id
            WHERE 1 = 1
        """);

        if (movieTitle != null &&  !movieTitle.isEmpty()) {
            sql.append("""
                AND LOWER(m.title) LIKE LOWER(:movie_title)
            """);
        }
        if (date != null) {
            sql.append("""
                AND s.start_time >= :startTime
                AND s.start_time < :endTime
            """);
        }

        JdbcClient.StatementSpec query = jdbcClient.sql(sql.toString());

        if (movieTitle != null &&  !movieTitle.isEmpty()) {
            query.param("movie_title", "%" + movieTitle + "%");
        }

        if (date != null) {
            query.param("startTime", date.atStartOfDay());
            query.param("endTime", date.plusDays(1).atStartOfDay());
        }

        return query.query(Long.class).single();
    }


}
