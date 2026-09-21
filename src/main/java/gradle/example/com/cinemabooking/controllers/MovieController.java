package gradle.example.com.cinemabooking.controllers;

import gradle.example.com.cinemabooking.generated.api.MoviesApi;
import gradle.example.com.cinemabooking.generated.model.CreateMovieRequest;
import gradle.example.com.cinemabooking.generated.model.MovieResponse;
import gradle.example.com.cinemabooking.generated.model.UpdateMovieRequest;
import gradle.example.com.cinemabooking.mappers.MovieMapper;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MovieController implements MoviesApi {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    public ResponseEntity<List<MovieResponse>> getMovies() {
        List<MovieResponse> response = movieService.getAll()
                .stream()
                .map(MovieMapper::toMovieResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    public ResponseEntity<MovieResponse> getMovieById(Long id) {
        Movie movie = movieService.getMovie(id);
        return ResponseEntity.ok(MovieMapper.toMovieResponse(movie));
    }

    public ResponseEntity<MovieResponse> createMovie(CreateMovieRequest movie) {
        Movie createdMovie = movieService.add(movie);

        return ResponseEntity.status(HttpStatus.CREATED).body(MovieMapper.toMovieResponse(createdMovie));
    }

    public ResponseEntity<Void> deleteMovie(Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<MovieResponse> updateMovie(Long id, UpdateMovieRequest movie) {
        Movie updatedMovie = movieService.update(id, movie);
        return ResponseEntity.status(HttpStatus.OK).body(MovieMapper.toMovieResponse(updatedMovie));
    }
}
