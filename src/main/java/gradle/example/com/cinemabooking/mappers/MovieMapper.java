package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.MovieResponse;
import gradle.example.com.cinemabooking.models.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    public static MovieResponse toMovieResponse(Movie movie) {
        MovieResponse movieResponse = new MovieResponse(movie.getId(), movie.getTitle(), movie.getDuration(), movie.getAge_rating(), movie.getDescription());
        return movieResponse;
    }
}
