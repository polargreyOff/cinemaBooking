package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.InvalidSessionTimeException;
import gradle.example.com.cinemabooking.exceptions.MovieSessionNotFound;
import gradle.example.com.cinemabooking.exceptions.SessionHasBookingsException;
import gradle.example.com.cinemabooking.exceptions.SessionTimeConflictException;
import gradle.example.com.cinemabooking.generated.model.CreateSessionRequest;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.repositories.MovieSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessionValidationService {
    private final MovieService movieService;
    private final HallService hallService;
    private final MovieSessionRepository movieSessionRepository;


    @Autowired
    public SessionValidationService(
            MovieService movieService,
            HallService hallService,
            MovieSessionRepository movieSessionRepository
    ) {
        this.movieService = movieService;
        this.hallService = hallService;
        this.movieSessionRepository = movieSessionRepository;
    }

    public void validateDelete(Long id) {
        movieSessionRepository.findById(id).orElseThrow(
                () -> new MovieSessionNotFound(id)
        );

        int bookingsCount = movieSessionRepository.countBookings(id);
        if (bookingsCount > 0) {
            throw new SessionHasBookingsException(id);
        }
    }

    public void validateCreate(CreateSessionRequest request) {
        Movie movie = movieService.getMovie(request.getMovieId());

        hallService.getHall(request.getHallId());

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

        checkOverlaps(request.getHallId(), startTime, endTime);
        isCorrectStartTime(startTime);
    }

    private boolean checkOverlaps(Long hallId, LocalDateTime startTime, LocalDateTime endTime) {
        Integer countOverlaps = movieSessionRepository.countOverlaps(hallId, startTime, endTime);
        if (countOverlaps > 0) {
            throw new SessionTimeConflictException(hallId);
        }
        return true;
    }

    private boolean isCorrectStartTime(LocalDateTime startTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidSessionTimeException();
        }
        return true;
    }
}
