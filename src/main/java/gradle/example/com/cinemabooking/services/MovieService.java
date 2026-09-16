package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.dto.CreateMovieRequest;
import gradle.example.com.cinemabooking.dto.UpdateMovieRequest;
import gradle.example.com.cinemabooking.exceptions.InvalidMovieException;
import gradle.example.com.cinemabooking.exceptions.MovieNotFoundException;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie getMovie(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
    }

    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    public Movie add(CreateMovieRequest request) {
        Movie movie = new Movie(
                null,
                request.getTitle(),
                request.getDuration(),
                request.getAge_rating(),
                request.getDescription()
        );

        return movieRepository.save(movie);
    }

    public void delete(Long id) {
        int deletedRows = movieRepository.deleteById(id);
        if (deletedRows <= 0) {
            throw new MovieNotFoundException(id);
        }

    }

    public Movie update(Long id, UpdateMovieRequest request) {
        Movie movie =  getMovie(id);
        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) {
                throw new InvalidMovieException("Movie title must not be empty");
            }

            movie.setTitle(request.getTitle());
        }

        if (request.getDuration() != null) {
            movie.setDuration(request.getDuration());
        }

        if (request.getAge_rating() != null) {
            movie.setAge_rating(request.getAge_rating());
        }

        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                throw new InvalidMovieException("Movie description must not be empty");
            }

            movie.setDescription(request.getDescription());
        }

        return movieRepository.update(movie);
    }
}
