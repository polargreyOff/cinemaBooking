package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.mappers.MovieSessionMapper;
import gradle.example.com.cinemabooking.models.MovieSession;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.time.LocalDate;
import java.util.List;

public class MovieSessionRepositoryCustomImpl implements MovieSessionRepositoryCustom {
    private final JdbcClient jdbcClient;
    private final MovieSessionMapper movieSessionMapper;

    public MovieSessionRepositoryCustomImpl(
            JdbcClient jdbcClient,
            MovieSessionMapper movieSessionMapper
    ) {
        this.jdbcClient = jdbcClient;
        this.movieSessionMapper = movieSessionMapper;
    }

    @Override
    public List<MovieSession> findAll(
            String movieTitle,
            LocalDate date,
            int page,
            int size,
            String sort,
            String direction
    ) {
        StringBuilder sql = new StringBuilder(MovieSessionRepository.SESSION_SELECT);
        sql.append(" WHERE 1 = 1 ");

        if (movieTitle != null && !movieTitle.isBlank()) {
            sql.append(" AND LOWER(m.title) LIKE LOWER(:movieTitle) ");
        }

        if (date != null) {
            sql.append("""
                AND s.start_time >= :startTime
                AND s.start_time < :endTime
                """);
        }

        String orderBy = switch (sort == null ? "" : sort) {
            case "price" -> "s.price";
            case "movieTitle" -> "m.title";
            default -> "s.start_time";
        };
        String orderDirection = "desc".equalsIgnoreCase(direction) ? "DESC" : "ASC";

        sql.append(" ORDER BY ")
                .append(orderBy)
                .append(' ')
                .append(orderDirection)
                .append(" LIMIT :size OFFSET :offset");

        JdbcClient.StatementSpec query = jdbcClient.sql(sql.toString())
                .param("size", size)
                .param("offset", (long) page * size);

        if (movieTitle != null && !movieTitle.isBlank()) {
            query = query.param("movieTitle", "%" + movieTitle + "%");
        }

        if (date != null) {
            query = query
                    .param("startTime", date.atStartOfDay())
                    .param("endTime", date.plusDays(1).atStartOfDay());
        }

        return query.query(movieSessionMapper).list();
    }
}
