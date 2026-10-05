package gradle.example.com.cinemabooking.controllers;


import gradle.example.com.cinemabooking.generated.api.SessionsApi;
import gradle.example.com.cinemabooking.generated.model.CreateSessionRequest;
import gradle.example.com.cinemabooking.generated.model.SessionPageResponse;
import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.generated.model.SessionSeatResponse;
import gradle.example.com.cinemabooking.services.MovieSessionService;
import gradle.example.com.cinemabooking.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class MovieSessionController implements SessionsApi {
    private final MovieSessionService movieSessionService;
    private final SeatService seatService;

    @Autowired
    public MovieSessionController(MovieSessionService movieSessionService, SeatService seatService) {
        this.movieSessionService = movieSessionService;
        this.seatService = seatService;
    }

    @Override
    public ResponseEntity<SessionResponse> getSessionById(Long id) {
        SessionResponse response = movieSessionService.getById(id);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SessionPageResponse> getSessions(
            String movieTitle,
            LocalDate date,
            Integer page,
            Integer size,
            String sort,
            String direction
    ) {
        SessionPageResponse response = movieSessionService.getSessions(
                movieTitle, date, page, size, sort, direction);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SessionResponse> createSession(CreateSessionRequest request) {
        SessionResponse response =  movieSessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteSession(Long id) {
        movieSessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<SessionSeatResponse>> getSessionSeats(Long sessionId) {
        return ResponseEntity.ok(seatService.getSessionSeats(sessionId));
    }
}
