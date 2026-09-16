package gradle.example.com.cinemabooking.controllers;

import gradle.example.com.cinemabooking.dto.CreateMovieRequest;
import gradle.example.com.cinemabooking.dto.UpdateMovieRequest;
import gradle.example.com.cinemabooking.generated.api.MoviesApi;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getMovies() {
        return movieService.getAll();
    }

    @GetMapping("/{id}")
    public Movie getMovie(@PathVariable Long id) {
        return movieService.getMovie(id);
    }

    @PostMapping
    public ResponseEntity<Movie> add(@Valid @RequestBody CreateMovieRequest movie) {
        Movie createdMovie = movieService.add(movie);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> update(@PathVariable Long id, @Valid @RequestBody UpdateMovieRequest movie) {
        Movie updatedMovie = movieService.update(id, movie);
        return ResponseEntity.status(HttpStatus.OK).body(updatedMovie);
    }
}
