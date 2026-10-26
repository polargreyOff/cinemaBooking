package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.InvalidSessionTimeException;
import gradle.example.com.cinemabooking.exceptions.MovieSessionNotFound;
import gradle.example.com.cinemabooking.exceptions.SessionHasBookingsException;
import gradle.example.com.cinemabooking.exceptions.SessionTimeConflictException;
import gradle.example.com.cinemabooking.generated.model.CreateSessionRequest;
import gradle.example.com.cinemabooking.generated.model.UpdateSessionRequest;
import gradle.example.com.cinemabooking.models.Hall;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.models.MovieSession;
import gradle.example.com.cinemabooking.repositories.MovieSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessionValidationService {
    private final MovieService movieService;
    private final HallService hallService;
    private final MovieSessionRepository movieSessionRepository;
    private final BookingQueryService bookingQueryService;


    @Autowired
    public SessionValidationService(
            MovieService movieService,
            HallService hallService,
            MovieSessionRepository movieSessionRepository,
            BookingQueryService bookingQueryService
    ) {
        this.movieService = movieService;
        this.hallService = hallService;
        this.movieSessionRepository = movieSessionRepository;
        this.bookingQueryService = bookingQueryService;
    }

    public void validateDelete(Long id) {
        movieSessionRepository.findById(id).orElseThrow(
                () -> new MovieSessionNotFound(id)
        );

        ensureNoBookings(id);
    }

    public UpdateSessionRequest validatePatchBooking(MovieSession session,  UpdateSessionRequest request) {

        ensureNoBookings(session.getId());

        UpdateSessionRequest body = new UpdateSessionRequest();


        if (request.getMovieId() == null) {
            body.setMovieId(session.getMovieId());
        } else {
            body.setMovieId(request.getMovieId());
        }
        if (request.getHallId() == null) {
            body.setHallId(session.getHallId());
        } else {
            body.setHallId(request.getHallId());
        }
        if (request.getStartTime() == null) {
            body.setStartTime(session.getStartTime());
        } else {
            body.setStartTime(request.getStartTime());
        }
        if (request.getPrice()== null) {
            body.setPrice(session.getPrice());
        } else {
            body.setPrice(request.getPrice());
        }


        Movie movie = movieService.getMovie(body.getMovieId());
        Hall hall = hallService.getHall(body.getHallId());

        LocalDateTime startTime = body.getStartTime();
        LocalDateTime endTime = body.getStartTime().plusMinutes(movie.getDuration());

        isCorrectStartTime(startTime);
        
        checkOverlapsExcludingSession(
                hall.getId(),
                startTime,
                endTime,
                session.getId()
        );

        return body;
    }


    public void validateCreate(CreateSessionRequest request) {
        Movie movie = movieService.getMovie(request.getMovieId());

        hallService.getHall(request.getHallId());

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

        checkOverlaps(request.getHallId(), startTime, endTime);
        isCorrectStartTime(startTime);
    }

    private void checkOverlaps(Long hallId, LocalDateTime startTime, LocalDateTime endTime) {
        checkOverlapsExcludingSession(hallId, startTime, endTime, null);
    }

    private void checkOverlapsExcludingSession(
            Long hallId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long sessionId
    ) {
        for (MovieSession existing : movieSessionRepository.findByHallId(hallId)) {
            if (existing.getId().equals(sessionId)) continue;
            LocalDateTime existingEnd = existing.getStartTime()
                    .plusMinutes(movieService.getMovie(existing.getMovieId()).getDuration());
            if (startTime.isBefore(existingEnd) && endTime.isAfter(existing.getStartTime())) {
                throw new SessionTimeConflictException(hallId);
            }
        }
    }

    private void ensureNoBookings(Long id) {
        if (bookingQueryService.hasBookingsForSession(id)) {
            throw new SessionHasBookingsException(id);
        }
    }

    private boolean isCorrectStartTime(LocalDateTime startTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidSessionTimeException();
        }
        return true;
    }
}
