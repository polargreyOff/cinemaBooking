package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.*;
import gradle.example.com.cinemabooking.generated.model.CreateSessionRequest;
import gradle.example.com.cinemabooking.generated.model.SessionPageResponse;
import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.generated.model.UpdateSessionRequest;
import gradle.example.com.cinemabooking.mappers.MovieSessionMapper;
import gradle.example.com.cinemabooking.models.MovieSession;
import gradle.example.com.cinemabooking.repositories.MovieSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieSessionService {
    private static final int MAX_PAGE_SIZE = 100;
    private final MovieSessionRepository movieSessionRepository;
    private final MovieSessionMapper sessionMapper;
    private final SessionValidationService sessionValidationService;

    @Autowired
    public MovieSessionService(
            MovieSessionRepository movieSessionRepository,
            MovieSessionMapper sessionMapper,
            SessionValidationService sessionValidationService
    ) {
        this.movieSessionRepository = movieSessionRepository;
        this.sessionMapper = sessionMapper;
        this.sessionValidationService = sessionValidationService;
    }

    public SessionPageResponse getSessions(
            String movieTitle,
            LocalDate date,
            Integer page,
            Integer size,
            String sort,
            String direction
    ) {
        int actualPage = page == null ? 0 : page;
        int actualSize;
        if (size == null || size > MAX_PAGE_SIZE) {
            actualSize = 20;
        } else {
            actualSize = size;
        }

        List<MovieSession> sessions = movieSessionRepository.findAll(
                movieTitle,
                date,
                actualPage,
                actualSize,
                sort,
                direction
        );

        Long totalElements = movieSessionRepository.count(movieTitle, date);

        int totalPages = (int)Math.ceil((double)totalElements/actualSize);

        SessionPageResponse response =
                new SessionPageResponse();

        response.setContent(
                sessions.stream()
                        .map(sessionMapper::toSessionResponse)
                        .toList()
        );

        response.setPage(actualPage);
        response.setSize(actualSize);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);

        return response;
    }

    public SessionResponse getById(Long id) {
        MovieSession session = movieSessionRepository.findById(id).orElseThrow(
                () -> new MovieSessionNotFound(id)
        );

        return sessionMapper.toSessionResponse(session);
    }

    public SessionResponse createSession(CreateSessionRequest request) {
        sessionValidationService.validateCreate(request);

        Long id = movieSessionRepository.save(
                request.getMovieId(),
                request.getHallId(),
                request.getStartTime(),
                request.getPrice()
        );

        MovieSession movieSession = movieSessionRepository.findById(id).orElseThrow(
                () -> new MovieSessionNotFound(id)
        );

        return sessionMapper.toSessionResponse(movieSession);
    }

    public void deleteSession(Long id) {
        sessionValidationService.validateDelete(id);
        movieSessionRepository.deleteById(id);
    }

    @Transactional
    public SessionResponse patchSession(Long id, UpdateSessionRequest request) {
        MovieSession session = movieSessionRepository.findById(id).orElseThrow(
                () -> {throw new MovieSessionNotFound(id);}
        );
        UpdateSessionRequest body = sessionValidationService.validatePatchBooking(session, request);
        session.setMovieId(body.getMovieId());
        session.setHallId(body.getHallId());
        session.setStartTime(body.getStartTime());
        session.setPrice(body.getPrice());

        MovieSession saved = movieSessionRepository.patch(session);
        return sessionMapper.toSessionResponse(saved);
    }
}