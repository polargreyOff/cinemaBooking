package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.models.Hall;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.models.MovieSession;
import org.springframework.stereotype.Component;

@Component
public class MovieSessionMapper {
    public SessionResponse toSessionResponse(MovieSession session, Movie movie, Hall hall) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setMovie(MovieMapper.toMovieResponse(movie));
        response.setHall(HallMapper.toHallResponse(hall));
        response.setStartTime(session.getStartTime());
        response.setPrice(session.getPrice());
        response.setEndTime(session.getStartTime().plusMinutes(movie.getDuration()));
        return response;
    }
}
