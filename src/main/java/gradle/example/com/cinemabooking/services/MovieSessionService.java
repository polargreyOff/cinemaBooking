package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.MovieSessionNotFound;
import gradle.example.com.cinemabooking.generated.model.CreateSessionRequest;
import gradle.example.com.cinemabooking.generated.model.SessionPageResponse;
import gradle.example.com.cinemabooking.generated.model.SessionResponse;
import gradle.example.com.cinemabooking.generated.model.UpdateSessionRequest;
import gradle.example.com.cinemabooking.mappers.MovieSessionMapper;
import gradle.example.com.cinemabooking.models.Hall;
import gradle.example.com.cinemabooking.models.Movie;
import gradle.example.com.cinemabooking.models.MovieSession;
import gradle.example.com.cinemabooking.repositories.MovieSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MovieSessionService {
    private static final int MAX_PAGE_SIZE = 100;
    private final MovieSessionRepository movieSessionRepository;
    private final MovieSessionMapper sessionMapper;
    private final SessionValidationService sessionValidationService;
    private final MovieService movieService;
    private final HallService hallService;

    public MovieSessionService(
            MovieSessionRepository movieSessionRepository,
            MovieSessionMapper sessionMapper,
            SessionValidationService sessionValidationService,
            MovieService movieService,
            HallService hallService
    ) {
        this.movieSessionRepository = movieSessionRepository;
        this.sessionMapper = sessionMapper;
        this.sessionValidationService = sessionValidationService;
        this.movieService = movieService;
        this.hallService = hallService;
    }

    @Transactional(readOnly = true)
    public SessionPageResponse getSessions(
            String movieTitle, LocalDate date, Integer page, Integer size, String sort, String direction
    ) {
        int actualPage = page == null ? 0 : Math.max(0, page);
        int actualSize = size == null || size < 1 || size > MAX_PAGE_SIZE ? 20 : size;
        String titleQuery = movieTitle == null ? "" : movieTitle.strip().toLowerCase(Locale.ROOT);
        Map<Long, Movie> movieCache = new HashMap<>();
        Map<Long, Hall> hallCache = new HashMap<>();
        List<SessionListing> listings = new ArrayList<>();

        for (MovieSession session : movieSessionRepository.findAll()) {
            if (date != null && !session.getStartTime().toLocalDate().equals(date)) continue;
            Movie movie = movieCache.computeIfAbsent(session.getMovieId(), movieService::getMovie);
            if (!movie.getTitle().toLowerCase(Locale.ROOT).contains(titleQuery)) continue;
            Hall hall = hallCache.computeIfAbsent(session.getHallId(), hallService::getHall);
            listings.add(new SessionListing(session, movie, hall));
        }

        Comparator<SessionListing> comparator = switch (sort == null ? "" : sort) {
            case "price" -> Comparator.comparing(item -> item.session().getPrice());
            case "movieTitle" -> Comparator.comparing(item -> item.movie().getTitle(), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(item -> item.session().getStartTime());
        };
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        listings.sort(comparator.thenComparing(item -> item.session().getId()));

        long total = listings.size();
        long offset = (long) actualPage * actualSize;
        List<SessionResponse> content = offset >= total ? List.of() : listings
                .subList((int) offset, (int) Math.min(offset + actualSize, total))
                .stream()
                .map(item -> sessionMapper.toSessionResponse(item.session(), item.movie(), item.hall()))
                .toList();

        SessionPageResponse response = new SessionPageResponse();
        response.setContent(content);
        response.setPage(actualPage);
        response.setSize(actualSize);
        response.setTotalElements(total);
        response.setTotalPages((int) Math.ceil((double) total / actualSize));
        return response;
    }

    @Transactional(readOnly = true)
    public SessionResponse getById(Long id) {
        MovieSession session = movieSessionRepository.findById(id)
                .orElseThrow(() -> new MovieSessionNotFound(id));
        return toResponse(session);
    }

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        sessionValidationService.validateCreate(request);
        Long id = movieSessionRepository.save(
                request.getMovieId(), request.getHallId(), request.getStartTime(), request.getPrice());
        MovieSession session = movieSessionRepository.findById(id)
                .orElseThrow(() -> new MovieSessionNotFound(id));
        return toResponse(session);
    }

    @Transactional
    public void deleteSession(Long id) {
        sessionValidationService.validateDelete(id);
        movieSessionRepository.deleteById(id);
    }

    @Transactional
    public SessionResponse patchSession(Long id, UpdateSessionRequest request) {
        MovieSession session = movieSessionRepository.findById(id)
                .orElseThrow(() -> new MovieSessionNotFound(id));
        UpdateSessionRequest body = sessionValidationService.validatePatchBooking(session, request);
        session.setMovieId(body.getMovieId());
        session.setHallId(body.getHallId());
        session.setStartTime(body.getStartTime());
        session.setPrice(body.getPrice());
        return toResponse(movieSessionRepository.patch(session));
    }

    private SessionResponse toResponse(MovieSession session) {
        Movie movie = movieService.getMovie(session.getMovieId());
        Hall hall = hallService.getHall(session.getHallId());
        return sessionMapper.toSessionResponse(session, movie, hall);
    }

    private record SessionListing(MovieSession session, Movie movie, Hall hall) {}
}
