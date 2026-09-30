package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.models.Hall;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.models.MovieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MovieSessionMapper {
    private final MovieMapper movieMapper;
    private final HallMapper hallMapper;

    @Autowired
    public MovieSessionMapper(MovieMapper movieMapper, HallMapper hallMapper) {
        this.movieMapper = movieMapper;
        this.hallMapper = hallMapper;
    }

    public MovieSession ResultSetToMovieSession(ResultSet rs, int rowNum)
            throws SQLException {

        Movie movie = new Movie(
                rs.getLong("movie_id"),
                rs.getString("movie_title"),
                rs.getInt("movie_duration"),
                rs.getString("movie_age_rating"),
                rs.getString("movie_description")
        );

        Hall hall = new Hall(
                rs.getLong("hall_id"),
                rs.getInt("hall_number")
        );

        return new MovieSession(
                rs.getLong("session_id"),
                movie,
                hall,
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getInt("price")
        );
    }

    public SessionResponse toSessionResponse(MovieSession movieSession) {

        SessionResponse response = new SessionResponse();

        response.setId(movieSession.getId());
        response.setMovie(movieMapper.toMovieResponse(movieSession.getMovie()));
        response.setHall(hallMapper.toHallResponse(movieSession.getHall()));
        response.setStartTime(movieSession.getStartTime());
        response.setPrice(movieSession.getPrice());
        response.setEndTime(movieSession.getStartTime().plusMinutes(movieSession.getMovie().getDuration()));

        return response;
    }
}
